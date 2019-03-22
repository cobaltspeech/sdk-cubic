# cli-client

## Overview

This folder (and resulting binary) can be used to send audio file(s) to a running instance of a cubic server.  It consists of several sub-commands:

| `cubicsvr-client`                   | Prints the default help message. |
| `cubicsvr-client models`            | Displays the transcription models being served by the given instance. |
| `cubicsvr-client version`           | Displays the versions of both the client and the server. |
| `cubicsvr-client transcribe single` | Sends a single file to server for transcription. |
| `cubicsvr-client transcribe list`   | Sends multiple files to server for transcription in parallel. |

`[cmd] --help` can be run on any command for more details on usage and included flags.

## Building

The `Makefile` should provide all necessary commands.
Specifically `make build`.

## Running/testing

There are two audio files in the testdata folder.
They are US English, recorded at 16kHz.
The filenames contain the intended transcription.

As a quick start, these commands are provided as examples of how to use the binary.  They should be run from the current directory (`cli-client/`).

Note: These commands assume that the cubic server instance is available at `localhost:2727`.

```sh
# Display the versions of client and server
cubicsvr-client version \
    --address localhost:2727

# List available models.  Note: The listed modelIDs are used in transcription methods
cubicsvr-client models \
    --address localhost:2727

# Transcribe the single file this_is_a_test-en_us-16.wav.
## Should result in the transcription of "this is a test"
bin/cubicsvr-client transcribe single \
    --address localhost:2727 \
    --audioFile ./testdata/this_is_a_test-en_us-16.wav

# Transcribe the list of files defined at ./testdata/list.txt
## Should result in the transcription of "this is a test" and "the second test" printed to stdout
bin/cubicsvr-client transcribe list \
    --address localhost:2727 \
    --listFile ./testdata/list.txt

# Same as the previous `transcribe list` command, but redirects the results to the --outputFile.
## Include --overwriteOutputFile to allow existing results to be overwritten.
bin/cubicsvr-client transcribe list \
    --address localhost:2727 \
    --listFile ./testdata/list.txt \
    --outputFile ./testdata/out.txt \
    [--overwriteOutputFile]

# Same as the first `transcribe list` command, but sends up to two files at a time.
## Note that the server may place a limit to the maximum number of concurrent requests processed.
bin/cubicsvr-client transcribe list \
    --address localhost:2727 \
    --listFile ./testdata/list.txt
    --nConcurrent 2
```
