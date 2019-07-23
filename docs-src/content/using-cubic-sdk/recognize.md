---
title: "Synchronous Recognition"
weight: 23
---

{{% panel theme="info" header="Note" %}}
Synchronous recognize requests are suitable only for audio clips shorter than 30
seconds.  In general, it is strongly recommended that you use streaming
recognition.
{{% /panel %}}

The following example shows how to transcribe a short audio clip using Cubic's
Synchoronous Recognize Request. It is assumed that the audio file contains raw
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

	recResp, err := client.Recognize(context.Background(), cfg, f)

	for _, r := range recResp.Results {
		if !r.IsPartial {
			fmt.Println(r.Alternatives[0].Transcript)
		}
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

# open audio file 
audio = open('test.raw', 'rb')

resp = client.Recognize(cfg, audio)

for result in resp.results:
	if not result.is_partial:
		print(result.alternatives[0].transcript)

```
{{% /tab %}}

{{% tab "C#" %}}
``` c#
// Initialize a gRPC connection
var creds = Grpc.Core.ChannelCredentials.Insecure;
var channel = new Grpc.Core.Channel(url, creds);
var client = new CobaltSpeech.Cubic.Cubic.CubicClient(channel);

// List the available models
var listModelsRequest = new CobaltSpeech.Cubic.ListModelsRequest();
var models = client.ListModels(listModelsRequest);

var cfg = new CobaltSpeech.Cubic.RecognitionConfig
{
    // Use the first available model
    ModelId = models.Models[0].Id,
};

// Open the audio file.
FileStream file = File.OpenRead("test.raw")
var audio = new CobaltSpeech.Cubic.RecognitionAudio{
    Data = Google.Protobuf.ByteString.FromStream(file)
};

// Create the request
var request = new CobaltSpeech.Cubic.RecognizeRequest
{
    Config = cfg,
    Audio = audio,
};

// Send the request
var resp = client.Recognize(request);
foreach (var result in resp.Results)
{
    if (!result.IsPartial)
    {
        Console.WriteLine(result.Alternatives[0].Transcript)
    }
}

```
{{% /tab %}}

{{%/tabs %}}
