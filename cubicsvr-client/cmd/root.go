// Copyright (2019) Cobalt Speech and Language, Inc. All rights reserved.

package cmd

import (
	"fmt"
	"os"

	"github.com/spf13/cobra"
	// "github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	// pbduration "github.com/golang/protobuf/ptypes/duration"
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

func init() {
	rootCmd.AddCommand(versionCmd)
	rootCmd.AddCommand(modelsCmd)
	rootCmd.AddCommand(transcribeCmd)

	rootCmd.PersistentFlags().StringVarP(&cubicSvrAddress, "address", "a", "localhost:2727", "Address of running cubicsvr instance.  Format should be 'address:port'.")
	rootCmd.PersistentFlags().BoolVar(&verbose, "verbose", false, "If true, extra logging will be done.  Helful for debugging. (hidden from help messages.)")
	rootCmd.PersistentFlags().MarkHidden("verbose")
}
