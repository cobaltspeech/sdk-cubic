// Copyright (2019) Cobalt Speech and Language, Inc. All rights reserved.

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
	Use:   "single",
	Short: "Runs a singe audio file through cubic svr and returns the results to stdout.",
	Long:  ``,
	Run: func(cmd *cobra.Command, args []string) {
		transcribeSingleAudioFile()
	},
}

// Argument variables.
var audioFilePath string

// Initialize flags.
func init() {
	transcribeSingleCmd.Flags().StringVarP(&audioFilePath, "audioFile", "f", "", "Path to audio file")
}

// transcribeSingleAudioFile is the main function.
func transcribeSingleAudioFile() {
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
	client, err := cubic.NewClient(cubicSvrAddress, cubic.WithInsecure())
	defer client.Close()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error connecting to server: %v", err)
		os.Exit(1)
	}

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
