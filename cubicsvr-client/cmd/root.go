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
	"fmt"
	"io"
	"os"

	cubic "github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/spf13/cobra"
)

// rootCmd represents the base command when called without any subcommands
var rootCmd = &cobra.Command{
	Use:   "cubicsvr-client",
	Short: "cubicsvr-client is a command line interface for interacting with a running instance of cubicsvr",
	Long:  `cubicsvr-client is a command line interface for interacting with a running instance of cubicsvr`,
}

// Execute adds all child commands to the root command and sets flags appropriately.
// This is called by main.main(). It only needs to happen once to the rootCmd.
func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
}

var cubicSvrAddress string
var verbose bool
var insecure bool

func init() {
	rootCmd.AddCommand(versionCmd)
	rootCmd.AddCommand(modelsCmd)
	rootCmd.AddCommand(transcribeCmd)

	rootCmd.PersistentFlags().StringVarP(&cubicSvrAddress, "server", "s", "localhost:2727",
		"Address of running cubicsvr instance.  Format should be 'address:port'.")

	rootCmd.PersistentFlags().BoolVar(&insecure, "insecure", false,
		"By default, connections to the server are encrypted (TLS).  Include this flag if you want TLS disabled.")

	rootCmd.PersistentFlags().BoolVar(&verbose, "verbose", false,
		"If true, extra logging will be done.  Helful for debugging. (hidden from help messages.)")
	_ = rootCmd.PersistentFlags().MarkHidden("verbose") // Shouldn't fail.
}

// createClient is a helper function that is shared by models.go, transcribe.go, and version.go
func createClient() (*cubic.Client, error) {
	var client *cubic.Client
	var err error

	if insecure {
		client, err = cubic.NewClient(cubicSvrAddress, cubic.WithInsecure())
	} else {
		client, err = cubic.NewClient(cubicSvrAddress)
	}

	if err != nil {
		return nil, fmt.Errorf("unable to reach --server  '%s'", cubicSvrAddress)
	}

	return client, nil
}

// verbosePrintf is a helper function.  Use --verbose to allow these strings to be printed
func verbosePrintf(w io.Writer, format string, a ...interface{}) {
	if verbose {
		fmt.Fprintf(w, format, a...)
	}
}
