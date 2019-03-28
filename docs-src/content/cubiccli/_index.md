---
title: "Cubic CLI"
weight: 10
---

A reference implementation of a client using this SDK can be found at https://github.com/cobaltspeech/sdk-cubic/blob/master/tools/cubiccli/.

This reference implementation can also be used to interact with an instance of cubic server.
The binary is self-documented, so you may use the `--help` flag to get more information on any of the commands.

Available commands are

| Command               | Purpose |
| `cubiccli`            | Prints the default help message. |
| `cubiccli models`     | Displays the transcription models being served by the given instance. |
| `cubiccli version`    | Displays the versions of both the client and the server. |
| `cubiccli transcribe` | Sends audio file(s) to server for transcription. |

To interact with Cobalt Speech's demo server, use the flag `--server demo-cubic.cobaltspeech.com` with all requests.
