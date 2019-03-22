// Copyright (2019) Cobalt Speech and Language, Inc. All rights reserved.

package cmd

import (
	"bufio"
	"context"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"os"
	"path/filepath"
	"sort"
	"strings"
	"sync"

	cubic "github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	pbduration "github.com/golang/protobuf/ptypes/duration"
	"github.com/spf13/cobra"
)

// Cmd is the command wrapping the functionality of multiple files listed in a listfile through cubicsvr.
var transcribeListCmd = &cobra.Command{
	Use:   "list",
	Short: "Runs a multiple audio files through cubicsvr.",
	Long: `This command takes in a path to a listfile that contains a list of "UttIDs \t path/to/audio.file"
	and runs each audio file through cubicsvr.  See --help for more info on each specific flag.
	`,
	Run: func(cmd *cobra.Command, args []string) {
		transcribeListOfAudioFiles()
	},
}

// Argument variables.
var listFilePath, resultsFile string
var overwriteResultsFile bool
var nConcurrentRequests int

// Initialize flags.
func init() {
	transcribeListCmd.Flags().StringVarP(&listFilePath, "listFile", "f", "", "Path to list file")
	transcribeListCmd.Flags().StringVarP(&resultsFile, "outputFile", "o", "-", "file to send output to.  '-' indicates stdout.")
	transcribeListCmd.Flags().BoolVar(&overwriteResultsFile, "overwriteOutputFile", false, "True allows existing outputFile to be overwritten.  False protects existing file.")
	transcribeListCmd.Flags().IntVarP(&nConcurrentRequests, "nConcurrent", "n", 1, "number of concurrent requests to send to cubicsvr")
}

type inputs struct {
	uttID    string
	filepath string
	fileSize int64
}

type outputs struct {
	uttID         string
	segment       int
	transcription string
}

// transcribeListOfAudioFiles is the main function.
// It performs the following steps:
//   1. verifies that the flags are valid
//   2. organizes the input files
//   3. Starts up nConcurrent worker goroutines
//   4. passes all audiofiles to the workers
//   5. Collects the resulting transcription and outputs the results.
func transcribeListOfAudioFiles() {
	// Check for valid flags.
	if listFilePath == "" {
		fmt.Fprint(os.Stderr, "Error: --listFile must not be empty\n")
		os.Exit(1)
	}
	// Check for overwritting an existing results file.
	if resultsFile != "-" {
		// Check to see if file exists
		_, err := os.Stat(resultsFile)
		// If it does not exist, then great!
		if err == nil {
			// The file exists, since it didn't throw an error
			if overwriteResultsFile {
				// Print a warning
				fmt.Fprintf(os.Stdout,
					"\nWarning:--outputFile '%s' already exists.\n"+
						"The current contents will be overwritten.\n\n", resultsFile)
			} else {
				// Explain why we are quitting
				fmt.Fprintf(os.Stdout,
					"\nWarning: --outputFile '%s' already exists.\n"+
						"Use --overwriteOutputFile to continue or choose a different location.\n"+
						"\n"+
						"Aborting.\n\n", resultsFile)
				os.Exit(0)
			}
		} else {
			// We care about all errors, except the FileDoesntExist error.
			// That would indicate it is safe to procced with the program as normal
			if !os.IsNotExist(err) {
				fmt.Fprintf(os.Stderr, "Error while checking for existing --outputFile: %v\n", err)
				os.Exit(1)
			}
		}
	}

	// Get a reference to outputfile/stdout
	var outputWritter io.Writer
	if resultsFile == "-" {
		outputWritter = os.Stdout
	} else {
		if file, err := os.Create(resultsFile); err != nil {
			fmt.Fprintf(os.Stderr, "Error opening output file: %v\n", err)
			os.Exit(1)
		} else {
			outputWritter = file
		}
	}

	// Setup channels for communicating between the various goroutines
	fileChannel := make(chan inputs)
	resultsChannel := make(chan outputs)
	// TODO setup an error channel?

	// Set up a cubicsvr client
	client, err := cubic.NewClient(cubicSvrAddress, cubic.WithInsecure())
	if err != nil {
		fmt.Fprintf(os.Stderr, "Failed to connect to cubicsvr: %v\n", err)
		os.Exit(1)
	}

	// Load the files and place them in a channel
	if files, err := loadFiles(listFilePath); err != nil {
		fmt.Fprintf(os.Stderr, "Error loading files: %v\n", err)
		os.Exit(1)
	} else {
		// Run in a separate goroutine so we can manage the results in the current goroutine.
		go func() {
			for _, f := range files {
				fileChannel <- f
			}
			close(fileChannel)
		}()
	}

	// Open the audio files and transcribe them, in parallel
	wg := &sync.WaitGroup{}
	for i := 0; i < nConcurrentRequests; i++ {
		wg.Add(nConcurrentRequests)
		go runFiles(client, fileChannel, resultsChannel, wg)
	}
	// close the results channel once all the workers have finished their steps
	go func() {
		wg.Wait()
		close(resultsChannel)
	}()

	// Deal with the transcription results
	for result := range resultsChannel {
		fmt.Fprintf(outputWritter, "%s_%d\t%s\n", result.uttID, result.segment, result.transcription)
	}

	if resultsFile != "-" {
		fmt.Printf("\nFinished writting results to file '%s'!\n\n", resultsFile)
	}
}

func loadFiles(path string) ([]inputs, error) {
	file, err := os.Open(path)
	if err != nil {
		log.Fatal(err)
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
			fmt.Fprintf(os.Stderr,
				"Error parsing list file on line #%d, contents: '%s'.",
				lineNumber, txt)
			os.Exit(1)
		}

		// Convert relative paths to absolute paths
		fpath := arr[1]
		if !filepath.IsAbs(fpath) {
			fpath, err = filepath.Abs(filepath.Join(folder, fpath))
			if err != nil {
				fmt.Fprintf(os.Stderr,
					"Error converting audio file entry to absolute path\n"+
						"\tPath to listfile directory: '%s'\n"+
						"\tInput for entry (line #%d): '%s'\n"+
						"\tCombine path: '%s'\n"+
						"\tResulting AbsPath: '%s'\n"+
						"\tError: %v\n",
					folder, lineNumber, arr[1],
					filepath.Join(folder, fpath),
					fpath, err)
				os.Exit(1)
			}
		}

		// Find the file size
		fileSize := int64(-1)
		if f, err := os.Open(fpath); err != nil {
			fmt.Fprintf(os.Stderr, "Error opening file '%s' to get it's filesize.\n", fpath)
			os.Exit(1)
		} else {
			if fi, err := f.Stat(); err != nil {
				fmt.Fprintf(os.Stderr, "Error getting size of file '%s'\n", fpath)
				os.Exit(1)
			} else {
				fileSize = fi.Size()
			}
		}

		// Add the new entry to the list
		utterances = append(utterances, inputs{
			uttID:    arr[0],
			filepath: fpath,
			fileSize: fileSize,
		})
		lineNumber++
	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}

	// Sor the entries by filesize
	sort.Slice(utterances, func(i, j int) bool {
		return utterances[i].fileSize < utterances[j].fileSize
	})

	return utterances, nil
}

func runFiles(client *cubic.Client, fileChannel <-chan inputs, resultsChannel chan<- outputs, wg *sync.WaitGroup) {
	defer wg.Done()

	for input := range fileChannel {
		// Open the file
		// fmt.Printf("Opening file '%s'\n", input.filepath)
		audio, err := os.Open(input.filepath)
		if err != nil {
			fmt.Fprintf(os.Stderr, "Error opening file: %s\n", err)
			os.Exit(1)
		}

		// Counter for segments
		segmentID := 0

		// Create and send the Streaming Recognize config
		// fmt.Printf("Starting Stream\n")
		err = client.StreamingRecognize(context.Background(),
			&cubicpb.RecognitionConfig{
				ModelId:       model,
				AudioEncoding: cubicpb.RecognitionConfig_RAW_LINEAR16,
				IdleTimeout:   &pbduration.Duration{Seconds: 30},
			},
			audio, // The file to send
			func(response *cubicpb.RecognitionResponse) { // The callback for results
				// TODO fix the formatting of the output
				if str, err := json.Marshal(response.Results); err != nil {
					resultsChannel <- outputs{
						uttID:         input.uttID,
						segment:       segmentID,
						transcription: fmt.Sprintf("Error serializing the results to json: %v", err),
					}
					fmt.Fprintf(os.Stderr, "Error serializing the results to json: %v\n", err)
				} else {
					resultsChannel <- outputs{
						uttID:         input.uttID,
						segment:       segmentID,
						transcription: string(str),
					}
				}
				segmentID++
			})
		if err != nil {
			fmt.Fprintf(os.Stderr, "Error streaming the file: %v\n", err)
		}
	}
}
