---
title: "Streaming Recognition"
weight: 30
---
The following example shows how to transcribe an audio file using Cubic's
Streaming Recognize Request. It is assumed that the audio file contains raw
samples, PCM16SLE like Cubic expects.  We will query the server for available
models and use the first model to transcribe this speech.

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

	f, err := os.Open("test.raw")
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
{{%/tabs %}}
