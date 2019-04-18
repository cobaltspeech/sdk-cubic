---
title: "Streaming Recognition"
weight: 30
---
The following example shows how to transcribe an audio file using Cubic’s 
Streaming Recognize Request. The example uses a WAV file as input to the 
streaming recognition. We will query the server for available models and
use the first model to transcribe the speech.

<!--more-->

{{%tabs %}}
{{% tab "Go" %}}
``` go
package main

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
)

const serverAddr = "127.0.0.1:2727"

func main() {
	client, err := cubic.NewClient(serverAddr)
	if err != nil {
		log.Fatal(err)
	}
	defer client.Close()

	modelResp, err := client.ListModels(context.Background())
	if err != nil {
		log.Fatal(err)
	}

	// Use the first available model
	model := modelResp.Models[0]

	f, err := os.Open("test.wav")
	if err != nil {
		log.Fatal(err)
	}

	defer f.Close()

	cfg := &cubicpb.RecognitionConfig{
		ModelId: model.Id,
	}

	// define a callback function to handle results
	resultHandler := func(resp *cubicpb.RecognitionResponse) {
		for _, r := range resp.Results {
			if !r.IsPartial {
				fmt.Println(r.Alternatives[0].Transcript)
			}
		}
	}

	err = client.StreamingRecognize(context.Background(), cfg, f, resultHandler)
	if err != nil {
		log.Fatal(err)
	}

}
```
{{% /tab %}}

{{% tab "Python" %}}
``` python
import cubic

serverAddress = '127.0.0.1:2727'

client = cubic.Client(serverAddress)

# get list of available models
modelResp = client.ListModels()
for model in modelResp.models:
	print("ID = {}, Name = {}".format(model.id, model.name))

# use the first available model
model = modelResp.models[0]

cfg = cubic.RecognitionConfig(
    model_id = model.id
)

# client.StreamingRecognize takes any binary
# stream object that has a read(nBytes) method.
# The method should return nBytes from the stream.

# open audio file stream
audio = open('test.wav', 'rb')

# send streaming request to cubic and 
# print out results as they come in
for resp in client.StreamingRecognize(cfg, audio):
    for result in resp.results:
		if result.is_partial:
			print("\r{0}".format(result.alternatives[0].transcript), end="")
		else:
			print("\r{0}".format(result.alternatives[0].transcript), end="\n")

```
{{% /tab %}}

{{%/tabs %}}