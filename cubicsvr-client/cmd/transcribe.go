// Copyright (2019) Cobalt Speech and Language, Inc. All rights reserved.

package cmd

import (
	"github.com/spf13/cobra"
)

// Cmd is the command wrapping sub commands used to run audio file through cubicsvr.
var transcribeCmd = &cobra.Command{
	Use:   "transcribe",
	Short: "Command for transcribing audio files",
	Long:  `This command has two sub-commands: single and list.  See the help on those commands for more`,
}

// Argument variables.
var model string

// var realtime bool

// Initialize flags.
func init() {
	transcribeCmd.AddCommand(transcribeSingleCmd)
	transcribeCmd.AddCommand(transcribeListCmd)

	transcribeCmd.PersistentFlags().StringVarP(&model, "model", "m", "1", "Selects which model ID to use for transcribing.")
	// transcribeCmd.PersistentFlags().BoolVarP(&realtime, "realtime", "r", false, "When true, a delay will be introduced as the audio file(s) are streamed, to simulate microphone input.")
}
