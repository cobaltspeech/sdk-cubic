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

{{% tab "Java/Android" %}}

Please note: this example does not attempt to handle threading and all exceptions.
It gives a simplified overview of the essential gRPC calls.

``` java
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import com.google.protobuf.ByteString;

import com.cobaltspeech.cubic.CubicGrpc;
import com.cobaltspeech.cubic.CubicOuterClass.*;

public static int main() {
    // Setup connection
    CubicGrpc.CubicStub mCubicService = CubicGrpc.newStub(
        ManagedChannelBuilder.forTarget(url).build());

    // Load the file into a ByteString for gRPC.
    byte[] fileContent = Files.readAllBytes("/path/to/file");
    ByteString fileContentBS = ByteString.copyFrom(fileContents)

    // Setup config message (Using first model available)
    RecognitionConfig cfg = RecognitionConfig.newBuilder()
        .setModelId("ModelID")
        .build()

    // Setup audio message
    RecognitionAudio audio = RecognitionAudio.newBuilder().setData(fileContentBS).build()

    // Setup callback to handle results
    StreamObserver<> responseObserver = new StreamObserver<RecognitionResponse>() {
        @Override
        public void onNext(RecognitionResponse value) {
            System.out.println("Result: " + value.toString());
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("Error with recognition:" + t.toString());
        }

        @Override
        public void onCompleted() {
            System.out.println("Server is done sending responses back");
        }
    };

    // Send it over to the server
    mCubicService.recognize(
        RecognizeRequest.newBuilder()
            .setConfig(cfg)
            .setAudio(audio)
            .build(),
        responseObserver);
}
```
{{% /tab %}}

{{% tab "Swift/iOS" %}}
``` swift

import swift_cubic
import GRPC
import NIO
import SwiftProtobuf

class CubicExample {
    
    let serverAddress = "demo-cubic.cobaltspeech.com"
    let serverPort = 2727
 
    private var eventLoopGroup: EventLoopGroup!
    private var client: Cobaltspeech_Cubic_CubicServiceClient!
    private var selectedModel: Cobaltspeech_Cubic_Model?

    init(useTLS: Bool) {
        # connect to Cubic server
        
        let target = ConnectionTarget.hostAndPort(serverAddress, serverPort)
        self.eventLoopGroup = PlatformSupport.makeEventLoopGroup(loopCount: 1, networkPreference: .best)
        let tls = useTLS ? ClientConnection.Configuration.TLS() : nil
        let configuration = ClientConnection.Configuration(target: target, 
                                                           eventLoopGroup: self.eventLoopGroup, 
                                                           errorDelegate: nil, 
                                                           connectivityStateDelegate: nil, 
                                                           tls: tls, 
                                                           connectionBackoff: nil)
        let connection = ClientConnection.init(configuration: configuration)
        self.client = Cobaltspeech_Cubic_CubicServiceClient(connection: connection)
        
        # list models
        
        let listModels = Cobaltspeech_Cubic_ListModelsRequest()
        
        client.listModels(listModels).response.whenComplete({ (result) in
            if let response = try? result.get() {
                self.selectedModel = response.models.first
                
                # open and recognize audio file
                self.uploadRecord()
            }
        })
    }

    func uploadRecord() {
        guard let selectedModel = selectedModel else { return }
        
        do {
            var req = Cobaltspeech_Cubic_RecognizeRequest()
            req.config = Cobaltspeech_Cubic_RecognitionConfig()
            req.config.modelID = selectedModel.id
            req.config.idleTimeout = Google_Protobuf_Duration()
            req.config.idleTimeout.seconds = 5
            req.config.audioEncoding = .rawLinear16 
            req.audio = Cobaltspeech_Cubic_RecognitionAudio()
            let audioUrl = URL(fileURLWithPath: "test.wav")
            req.audio.data = try Data(contentsOf: audioUrl)

            client.recognize(req).response.whenComplete({ (response) in
                if let response = try? response.get() {
                    for result in response.results {
                        if !result.isPartial, let alternative = result.alternatives.first {
                            print(alternative.transcript)
                        }
                    }
                }
            })
        } catch let error {
            print(error.localizedDescription)
        }
    }
}

```
{{% /tab %}}

{{%/tabs %}}
