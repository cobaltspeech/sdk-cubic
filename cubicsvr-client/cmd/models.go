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

	"github.com/spf13/cobra"
)

var modelsCmd = &cobra.Command{
	Use:   "models",
	Short: "Report cubicsvr models and return.",
	RunE: func(cmd *cobra.Command, args []string) error {
		// Create client connection
		verbosePrintf(os.Stdout, "Creating connection to server '%s'\n", cubicSvrAddress)
		client, err := createClient()
		if err != nil {
			return err
		}
		defer client.Close()

		// Grab the models and print them out
		verbosePrintf(os.Stdout, "Fetching models from server\n")
		resp, err := client.ListModels(context.Background())
		if err != nil {
			return fmt.Errorf("Unable to reach --server at '%s'", cubicSvrAddress)
		}

		// Display the models
		for _, m := range resp.Models {
			fmt.Printf("Model ID '%q': %s (Sample Rate:%6d)\n",
				m.Id, m.Name, m.Attributes.SampleRate)
		}

		return nil
	},
}
