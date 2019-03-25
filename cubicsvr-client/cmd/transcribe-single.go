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
	"context"
	"encoding/json"
	"fmt"
	"os"

	cubic "github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	pbduration "github.com/golang/protobuf/ptypes/duration"
	"github.com/spf13/cobra"
)

// Cmd is the command wrapping the functionality of running a single audio file through cubicsvr.
var transcribeSingleCmd = &cobra.Command{
	Use:   "single PATH [flags]",
	Short: "Runs a singe audio file through cubic svr and returns the results to stdout.",
	// Long:  ``,
	Run: func(cmd *cobra.Command, args []string) {
		var audioFilePath string
		if len(args) != 1 {
			fmt.Fprintf(os.Stdout, "\nError: Path to audio file must be included.\n\n")
			cmd.Usage()
			os.Exit(1)
		} else {
			audioFilePath = args[0]
		}
		transcribeSingleAudioFile(audioFilePath)
	},
}

// transcribeSingleAudioFile is the main function.
func transcribeSingleAudioFile(audioFilePath string) {
	// Error check flags
	if audioFilePath == "" {
		fmt.Printf("Error: --audioFile must be populated")
	}

	// Open the file
	fmt.Printf("Opening file '%s'\n", audioFilePath)
	audio, err := os.Open(audioFilePath)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error opening file: %s\n", err)
		os.Exit(1)
	}

	// Create client connection
	fmt.Printf("Connecting to %s\n", cubicSvrAddress)
	var client *cubic.Client
	if insecure {
		client, err = cubic.NewClient(cubicSvrAddress, cubic.WithInsecure())
	} else {
		client, err = cubic.NewClient(cubicSvrAddress)
	}
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error connecting to server: %v", err)
		os.Exit(1)
	}
	defer client.Close()

	// Create and send the Streaming Recognize config
	fmt.Printf("Starting Stream\n")
	err = client.StreamingRecognize(context.Background(),
		&cubicpb.RecognitionConfig{
			ModelId:       model,
			AudioEncoding: cubicpb.RecognitionConfig_RAW_LINEAR16,
			IdleTimeout:   &pbduration.Duration{Seconds: 30},
		},
		audio, // The file to send
		func(response *cubicpb.RecognitionResponse) { // The callback for results
			// TODO fix the formatting of the output
			if err := json.NewEncoder(os.Stdout).Encode(response.Results); err != nil {
				fmt.Fprintf(os.Stderr, "Error serializing the results to json: %v\n", err)
			}
		})
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error streaming the file: %v\n", err)
	}
}
