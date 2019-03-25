// Copyright (2019) Cobalt Speech and Language Inc.

// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package cmd

import (
	"bufio"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"os"
	"path/filepath"
	"strings"
	"sync"

	cubic "github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	pbduration "github.com/golang/protobuf/ptypes/duration"
	"github.com/spf13/cobra"
)

type inputs struct {
	uttID    string
	filepath string
}

type outputs struct {
	uttID    string
	segment  int
	response []*cubicpb.RecognitionResult
}

// Argument variables.
var model string
var inputFile string
var listFile bool
var resultsFile string
var nConcurrentRequests int

// Initialize flags.
func init() {
	transcribeCmd.PersistentFlags().StringVarP(&model, "model", "m", "1",
		"Selects which model ID to use for transcribing.")

	transcribeCmd.Flags().BoolVarP(&listFile, "listFile", "l", false,
		"When true, the PATH is pointing to a file containing a list of "+
			"'[UtteranceID]\t[path/to/audio.wav]', one entry per line.")

	transcribeCmd.Flags().StringVarP(&resultsFile, "outputFile", "o", "-",
		"file to send output to.  '-' indicates stdout.")

	transcribeCmd.Flags().IntVarP(&nConcurrentRequests, "workers", "n", 1,
		"number of concurrent requests to send to cubicsvr")
}

var longMsg = `
This command is used for transcribing audio files.
There are two modes: single file or list file.

In single file mode:
    the PATH should point to a single audio.wav file.

In list file mode:
    the PATH should point to a a file listing multiple audio files
    with the format 'Utterance_ID \t PATH \n'.
    Each entry should be on its own line and no spaces should present.

See 'transcribe --help' for details on the other flags.`

// Cmd is the command wrapping sub commands used to run audio file(s) through cubicsvr.
var transcribeCmd = &cobra.Command{
	Use:   "transcribe PATH [--list-file] [flags]",
	Short: "Command for transcribing audio file(s) through cubicsvr.",
	Long:  longMsg,
	Args: func(cmd *cobra.Command, args []string) error {
		if len(args) < 1 {
			return fmt.Errorf("requires a PATH argument")
		}
		inputFile = args[0]

		// Check for overwritting an existing results file.
		if resultsFile != "-" {
			// Check to see if file exists
			if _, err := os.Stat(resultsFile); err == nil {
				// The file exists, since it didn't throw an error, so explain why we are quitting
				return fmt.Errorf("Aborting because --outputFile '%s' already exists", resultsFile)
			} else if !os.IsNotExist(err) {
				// We care about all errors, except the FileDoesntExist error.
				// That would indicate it is safe to procced with the program as normal
				return fmt.Errorf("Error while checking for existing --outputFile: %v", err)
			}
		}

		return nil
	},
	RunE: func(cmd *cobra.Command, args []string) error {
		if err := transcribe(); err != nil {
			return err
		}
		return nil
	},
}

// transcribe is the main function.
// Flags have been previously verified in the cobra.Cmd.Args function above.
// It performs the following steps:
//   1. organizes the input file(s)
//   2. Starts up n [--workers] worker goroutines
//   3. passes all audiofiles to the workers
//   4. Collects the resulting transcription and outputs the results.
func transcribe() error {

	// Get output writter (file or stdout)
	outputWriter, err := getOutputWriter(resultsFile)
	if err != nil {
		return err
	}

	// Setup channels for communicating between the various goroutines
	fileChannel := make(chan inputs)
	resultsChannel := make(chan outputs)
	errChannel := make(chan error)

	// Set up a cubicsvr client
	client, err := createClient()
	if err != nil {
		return err
	}
	defer client.Close()

	// Load the files and place them in a channel
	files, err := loadFiles(inputFile)
	if err != nil {
		return fmt.Errorf("Error loading file(s): %v", err)
	}
	verbosePrintf(os.Stdout, "Found %d files.\n", len(files))

	// Starts a goroutine that ads files to the channel and closes
	// it when there are no more files to add.
	feedInputFiles(fileChannel, files)

	// Starts multipe goroutines that each pull from the fileChannel,
	// send requests to cubic server, and then adds the results to the
	// results channel
	startWorkers(client, fileChannel, resultsChannel, errChannel)

	// Handle errors
	wg := sync.WaitGroup{}
	wg.Add(1)
	go func() {
		verbosePrintf(os.Stdout, "Watching for errors during process.\n")
		for err := range errChannel {
			fmt.Fprintf(os.Stderr, "%v\n", err)
		}
		wg.Done()
	}()

	// Deal with the transcription results
	processResults(outputWriter, resultsChannel)

	wg.Wait()
	return nil
}

// Get a reference to outputfile/stdout, depending on the input of
func getOutputWriter(out string) (io.Writer, error) {
	if out == "-" {
		return os.Stdout, nil
	}

	file, err := os.Create(out)
	if err != nil {
		return nil, fmt.Errorf("Error opening output file: %v", err)
	}
	return file, nil
}

func loadFiles(path string) ([]inputs, error) {
	if listFile {
		return loadListFiles(path)
	}
	return loadSingleFile(path)
}

func loadSingleFile(path string) ([]inputs, error) {
	return []inputs{
		inputs{uttID: "Utterance", filepath: path},
	}, nil
}

func loadListFiles(path string) ([]inputs, error) {
	file, err := os.Open(path)
	if err != nil {
		return nil, err
	}
	defer file.Close()

	folder := filepath.Dir(path)

	scanner := bufio.NewScanner(file)
	lineNumber := 0
	utterances := make([]inputs, 0)
	for scanner.Scan() {
		txt := scanner.Text()
		arr := strings.Split(txt, "\t")
		if len(arr) != 2 {
			return nil, fmt.Errorf("Error parsing list file on line #%d, "+
				"format should be '[UttID]\\t[path/to/audio.wav]'.  "+
				"Line contents: '%s'", lineNumber, txt)
		}

		// Convert relative paths to absolute paths
		fpath := arr[1]
		if !filepath.IsAbs(fpath) {
			fpath, err = filepath.Abs(filepath.Join(folder, fpath))
			if err != nil {
				return nil, fmt.Errorf("Error converting audio file entry to absolute path\n"+
					"\tPath to listfile directory: '%s'\n"+
					"\tInput for entry (line #%d): '%s'\n"+
					"\tCombine path: '%s'\n"+
					"\tResulting AbsPath: '%s'\n"+
					"\tError: %v\n",
					folder, lineNumber, arr[1],
					filepath.Join(folder, fpath),
					fpath, err)
			}
		}

		// Add the new entry to the list
		utterances = append(utterances, inputs{
			uttID:    arr[0],
			filepath: fpath,
		})
		lineNumber++
	}

	if err := scanner.Err(); err != nil {
		return nil, err
	}

	return utterances, nil
}

func feedInputFiles(fileChannel chan<- inputs, files []inputs) {
	// Feed those files to the channel in a separate goroutine
	// so we can manage the results in the current goroutine.
	go func() {
		for _, f := range files {
			verbosePrintf(os.Stdout, "Feeding next file '%s'.\n", f.filepath)
			fileChannel <- f
		}
		verbosePrintf(os.Stdout, "Done feeding audio files.\n")
		close(fileChannel)
	}()
}

// Open the audio files and transcribe them, in parallel
func startWorkers(client *cubic.Client, fileChannel <-chan inputs,
	resultsChannel chan<- outputs, errChannel chan<- error) {

	wg := &sync.WaitGroup{}

	verbosePrintf(os.Stdout, "Starting '%d' workers.\n", nConcurrentRequests)
	for i := 0; i < nConcurrentRequests; i++ {
		wg.Add(nConcurrentRequests)
		go transcribeFiles(i, wg, client, fileChannel, resultsChannel, errChannel)
	}

	// close the results channel once all the workers have finished their steps
	go func() {
		wg.Wait()
		close(resultsChannel)
		close(errChannel)
		verbosePrintf(os.Stdout, "Finished getting results back from cubicsvr for all files.\n")
	}()
}

func transcribeFiles(workerID int, wg *sync.WaitGroup, client *cubic.Client,
	fileChannel <-chan inputs, resultsChannel chan<- outputs, errChannel chan<- error) {
	verbosePrintf(os.Stdout, "Worker %d starting\n", workerID)
	for input := range fileChannel {

		// Open the file
		audio, err := os.Open(input.filepath)
		if err != nil {
			errChannel <- fmt.Errorf(
				"Error: skipping Utterance '%s', couldn't open file '%s'",
				input.uttID, input.filepath)
		}

		// Counter for segments
		segmentID := 0

		verbosePrintf(os.Stdout, "Worker%2d streaming Utterance '%s' (file '%s').\n",
			workerID, input.uttID, input.filepath)

		// Create and send the Streaming Recognize config
		err = client.StreamingRecognize(context.Background(),
			&cubicpb.RecognitionConfig{
				ModelId:       model,
				AudioEncoding: cubicpb.RecognitionConfig_WAV,
				IdleTimeout:   &pbduration.Duration{Seconds: 30},
			},
			audio, // The file to send
			func(response *cubicpb.RecognitionResponse) { // The callback for results
				verbosePrintf(os.Stdout, "Worker%2d recieved result segment #%d for Utterance '%s'.\n",
					workerID, segmentID, input.uttID)
				resultsChannel <- outputs{
					uttID:    input.uttID,
					segment:  segmentID,
					response: response.Results,
				}
				segmentID++
			})
		if err != nil {
			errChannel <- fmt.Errorf("error streaming the file '%s': unable to connect to --server at '%s'",
				input.filepath, cubicSvrAddress)
		}
	}
	verbosePrintf(os.Stdout, "Worker %d done\n", workerID)
	wg.Done()
}

func processResults(outputWriter io.Writer, resultsChannel <-chan outputs) {
	for result := range resultsChannel {
		for _, resp := range result.response {
			if !resp.IsPartial {
				if str, err := json.Marshal(resp); err != nil {
					fmt.Fprintf(os.Stderr, "[Error serializing]: %s_Segment%d\t%v\n", result.uttID, result.segment, err)
				} else {
					fmt.Fprintf(outputWriter, "%s_%d\t%s\n", result.uttID, result.segment, str)
				}
			}
		}
	}

	if resultsFile != "-" {
		fmt.Printf("\nFinished writting results to file '%s'!\n\n", resultsFile)
	}
}
