---
title: "Quick Start"
weight: 2
---

To evaluate Cobalt's speech recognition, you can access the [Cubic web demo](https://demo-cubic.cobaltspeech.com:8080) or can download a command-line interface built using our Go SDK to specify an audio file or list of files and get transcripts back from a running instance of cubicsvr. The [source code for cubic-cli](https://github.com/cobaltspeech/cubic-cli) is available to use as an example client.

The command-line tool can call a local instance of Cubic (see [Installing the Cubicsvr Image](/sdk-cubic/getting-started/cubic_docker)) or the demo server.

```bash
# Local instance
./bin/cubic-cli --insecure --server localhost:2727 transcribe ./test.wav

# Demo server
./bin/cubic-cli --server demo-cubic.cobaltspeech.com:2727 transcribe ./test.wav
```
   
Commercial use of the demo service is not permitted. This server is for testing and demonstration purposes only and is not guaranteed to support high availability or high volume. Data uploaded to the server may be stored for internal purposes.
