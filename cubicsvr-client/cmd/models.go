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
	var client *cubic.Client
	var err error
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
