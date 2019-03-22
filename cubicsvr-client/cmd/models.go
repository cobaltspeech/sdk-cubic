// Copyright (2019) Cobalt Speech and Language, Inc. All rights reserved.

package cmd

import (
	"context"
	"fmt"
	"os"

	cubic "github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/spf13/cobra"
)

var modelsCmd = &cobra.Command{
	Use:   "models",
	Short: "Report cubicsvr models and return.",
	Run: func(cmd *cobra.Command, args []string) {
		printModels()
	},
}

// printModels returns the list of available models on the server.
func printModels() {
	// Create client connection
	client, err := cubic.NewClient(cubicSvrAddress, cubic.WithInsecure())
	defer client.Close()
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error connecting to server: %v", err)
		os.Exit(1)
	}

	// Grab the models and print them out
	if resp, err := client.ListModels(context.Background()); err != nil {
		fmt.Fprintf(os.Stderr, "Error fetching cubicsvr models: %v\n", err)
		os.Exit(1)
	} else {
		for _, m := range resp.Models {
			fmt.Printf("Model ID%3s: '%s' (Sample Rate:%6d)\n",
				m.Id, m.Name, m.Attributes.SampleRate)
		}
	}
}
