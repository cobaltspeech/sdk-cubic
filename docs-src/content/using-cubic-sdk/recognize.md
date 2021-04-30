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

{{< tabs >}}

{{< tab "Go" "go" >}}
package main

import (
	"context"
	"fmt"
	"log"
	"os"

	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
)

func main() {

    // Creating client without TLS. Remove cubic.WithInsecure() if using TLS
    serverAddr := "127.0.0.1:2727"
	client, err := cubic.NewClient(serverAddr, cubic.WithInsecure())
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
{{< /tab >}}

{{< tab "Python" "python" >}}
import cubic

# set insecure to False if server uses TLS
serverAddress = '127.0.0.1:2727'
client = cubic.Client(serverAddress, insecure=True)

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
{{< /tab >}}

{{< tab "C++" "c++" >}}
#include "cubic_client.h"

#include <iostream>
#include <fstream>
#include <string>

namespace CubicPB = cobaltspeech::cubic;

const std::string serverAddress = "localhost:2727";
const std::string filename = "test.wav";

int main(int argc, char *argv[]) {
    CubicClient client(serverAddress);

    // Get the list of available models
    std::vector<CubicModel> models = client.listModels();
    std::cout << "Available Models:" << std::endl;
    for (const CubicModel &m : models) {
        std::cout << "ID = " << m.id() << ", Name = " << m.name() << std::endl;
    }

    // Use the first model to set up the recognition config
    auto modelID = models[0].id();
    CubicPB::RecognitionConfig cfg;
    cfg.set_model_id(modelID);
    cfg.set_audio_encoding(CubicPB::RecognitionConfig::WAV);

    // Read the entire audio file
    std::ifstream infile(filename);
    std::string data( (std::istreambuf_iterator<char>(infile)),
                      (std::istreambuf_iterator<char>()) );

    // Send the recognition request.
    CubicPB::RecognitionResponse resp = client.recognize(cfg, data.c_str(), data.length());

    // Print the results
    for (int i = 0; i < resp.results_size(); i++) {
        CubicPB::RecognitionResult result = resp.results(i);
        if (!result.is_partial()) {
            std::cout << result.alternatives(0).transcript() << std::endl;
        }
    }
}
{{< /tab >}}

{{< tab "C#" "c#" >}}
using System;
using System.IO;

namespace CubicSynchronousRecognitionExample {
    class Program {
        static void Main(string[] args) {
            // set creds = new Grpc.Core.SslCredentials(); if using TLS
            var serverAddress = "127.0.0.1:2727";
            var creds = Grpc.Core.ChannelCredentials.Insecure;

            // Initialize a gRPC connection
            var channel = new Grpc.Core.Channel(serverAddress, creds);
            var client = new CobaltSpeech.Cubic.Cubic.CubicClient(channel);

            // List the available models
            var listModelsRequest = new CobaltSpeech.Cubic.ListModelsRequest();
            var models = client.ListModels(listModelsRequest);

            var cfg = new CobaltSpeech.Cubic.RecognitionConfig {
                // Use the first available model
                ModelId = models.Models[0].Id,
            };

            // Open the audio file.
            FileStream file = File.OpenRead("test.raw");
            var audio = new CobaltSpeech.Cubic.RecognitionAudio {
                Data = Google.Protobuf.ByteString.FromStream(file)
            };

            // Create the request
            var request = new CobaltSpeech.Cubic.RecognizeRequest {
                Config = cfg,
                Audio = audio,
            };

            // Send the request
            var resp = client.Recognize(request);
            foreach (var result in resp.Results) {
                if (!result.IsPartial) {
                    Console.WriteLine(result.Alternatives[0].Transcript);
                }
            }
        }
    }
}
{{< /tab >}}

{{< tab "Java/Android" "java" >}}
/*
  Please note: this example does not attempt to handle threading and all exceptions.
  It gives a simplified overview of the essential gRPC calls.
*/

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
{{< /tab >}}

{{< tab "Swift/iOS" "swift" >}}
import Foundation
import Cubic

class CubicExample {

    // set useTLS to true if using TLS
    let client = Client(host: "127.0.0.1", port: 2727, useTLS: false)
    var config = Cobaltspeech_Cubic_RecognitionConfig()
    let fileName = "text.raw"

    public init() {
        config.audioEncoding = .rawLinear16

        client.listModels(success: { (models) in
            if let model = models?.first {
                self.config.modelID = model.id

                self.client.recognize(audioURL: URL(fileURLWithPath: fileName), config: self.config, success: { (response) in
                    for result in response.results {
                        if !result.isPartial, let alternative = result.alternatives.first {
                            print(alternative.transcript)
                        }
                    }
                }) { (error) in
                    print(error.localizedDescription)
                }
            }
        }) { (error) in
            print(error.localizedDescription)
        }
    }

}
{{< /tab >}}

{{< /tabs >}}
