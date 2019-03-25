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
