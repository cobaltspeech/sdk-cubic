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

var (
	commitHash string
	version    string
)

var versionCmd = &cobra.Command{
	Use:   "version",
	Short: "Report cubicsvr version and return.",
	Run: func(cmd *cobra.Command, args []string) {
		versionString()
	},
}

// versionString returns the build version of this binary.
func versionString() {
	// Print the client version
	if version == "" {
		version = "devel"
	}
	if commitHash == "" {
		commitHash = "nil"
	}
	fmt.Printf("Client: (Version: %s -- Commit: %s)\n", version, commitHash)

	// Get the version of the cubicsvr
	var client *cubic.Client
	var err error
	if insecure {
		client, err = cubic.NewClient(cubicSvrAddress, cubic.WithInsecure())
	} else {
		client, err = cubic.NewClient(cubicSvrAddress, cubic.WithServerCert([]byte{}))
	}
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error connecting to server: %v", err)
		os.Exit(1)
	} else {
		defer client.Close()
		if resp, err := client.Version(context.Background()); err != nil {
			fmt.Fprintf(os.Stderr, "Error fetching cubicsvr version: %v\n", err)
			os.Exit(1)
		} else {
			fmt.Printf("Cubic Server: (Cubic: %s -- Server: %s)\n", resp.Cubic, resp.Server)
			os.Exit(0)
		}
	}
}
