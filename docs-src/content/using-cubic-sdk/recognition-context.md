---
title: "Recognition Context"
weight: 25
---

Cubic allows users to send context information with a recognition request which
may aid the speech recognition. For example, if you have a list of names that
you want to make sure the Cubic model transcribes correctly, with the correct
spelling, then you may provide the list in the form of a
[`RecognitionContext`](../../protobuf/autogen-doc-cubic-proto/#message-recognitioncontext) object along with the [`RecognitionConfig`](../client-configs/#fields)
before streaming data.

Cubic models allow different sets of "context tokens" each of which can be
paired with a list of words or phrases. For example, a Cubic model may have a
context token for airport names, and you can provide a list of airport names you
want to be recognized correctly for this context token. Likewise, models may
also be configured with tokens for "contact list names", "menu items", "medical jargon" etc.

To ensure that there is no added latency in processing the list of words or
phrases during a recognition request, we have a API method called
[`CompileContext()`](../../protobuf/autogen-doc-cubic-proto/#service-cubic) that
allows the user to compile the list into a compact, fast to search format for
the Cubic model. Currently, we only accept list of phrases or words that have
been precompiled.

<!--more-->

### Compiling Recognition Context

We have several examples in different langagues below showing you how to compile
context data and send it during a recognition request.

{{%tabs %}}
{{% tab "Go" %}}

``` go
package main

import (
    "context"
    "fmt"
    "log"
    "os"
    "strings"

    "github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
    "github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
)

func main() {

    // creating client without TLS. Remove WithInsecure() if using TLS
    serverAddr := "127.0.0.1:2727"
    client, err := cubic.NewClient(serverAddr, cubic.WithInsecure())
    if err != nil {
        log.Fatal(err)
    }
    defer client.Close()

    // Get list of available models
    modelResp, err := client.ListModels(context.Background())
    if err != nil {
        log.Fatal(err)
    }
    for _, m := range modelResp.Models {
        fmt.Printf("\nID = %v, Name = %v, Supports Context = %v\n",
            m.Id, m.Name, m.Attributes.SupportsContext)
        // printing allowed context tokens
        if m.Attributes.SupportsContext {
            fmt.Printf("Allowed Context Tokens = %v\n\n",
                strings.Join(m.Attributes.AllowedContextTokens, ", "))
        }
    }

    // Assuming the first model supports context
    model := modelResp.Models[0]

    // Let's say this model has an allowed context token called "airport_names" and
    // we have a list of airport names that we want to make sure the recognizer gets
    // right. We can compile the list of names into a easy to search and decodable
    // format for the model using the CompileContext() method. We can save the compiled
    // data and send it back during a recognize request to aid the speech recognition.

    // a small example list
    phrases := []string{"NARITA", "KUALA LUMPUR INTERNATIONAL", "ISTANBUL ATATURK", "LAGUARDIA"}
    contextToken := model.Attributes.AllowedContextTokens[0] // "airport_names"

    // sending request to server
    compiledResp, err := client.CompileContext(
        context.Background(), model.Id, contextToken, phrases, nil)
    if err != nil {
        log.Fatal(err)
    }

    // saving the compiled result for later use; note this compiled data is only
    // compatible with the model whose ID was provied in the CompileContext call
    compiledContexts := make([]*cubicpb.CompiledContext, 0)
    compiledContexts = append(compiledContexts, compiledResp.Context)

    // Now we can send a recoginze request along with the compiled context. The
    // context data is provided through the recognition config as a list of compiled
    // contexts (i.e we can provide more than one compiled context if the model
    // supports more than one context token).
    cfg := &cubicpb.RecognitionConfig{
        ModelId:       model.Id,
        AudioEncoding: cubicpb.RecognitionConfig_WAV,
        Context:       &cubicpb.RecognitionContext{Compiled: compiledContexts},
    }

    // The rest is the same as a usual streaming recognize request

    // open audio file stream
    f, err := os.Open("test.wav")
    if err != nil {
        log.Fatal(err)
    }
    defer f.Close()

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
insecure = True # set to False if server uses TLS

client = cubic.Client(serverAddress, insecure=insecure)

# Get list of available models
modelResp = client.ListModels()
for m in modelResp.models:
    print("\nID = {}, Name = {}, Supports Context = {}".format(
        m.id, m.name, m.attributes.supports_context))
    if m.attributes.supports_context:
        # printing allowed context tokens
        print("Allowed context tokens =  {}\n".format(
            str(m.attributes.allowed_context_tokens)))

# Assuming the first model supports context
model = modelResp.models[0]

# Let's say this model has an allowed context token called "airport_names" and
# we have a list of airport names that we want to make sure the recognizer gets
# right. We can compile the list of names into a easy to search and decodable
# format for the model using the CompileContext() method. We can save the compiled
# data and send it back during a recognize request to aid the speech recognition.

# a small example list
phrases = ["NARITA", "KUALA LUMPUR INTERNATIONAL", "ISTANBUL ATATURK", "LAGUARDIA"]
contextToken = model.attributes.allowed_context_tokens[0]  # "airport_names"

# sending request to server
compiledResp = client.CompileContext(model.id, contextToken, phrases)

# saving the compiled result for later use; note this compiled data is only
# compatible with the model whose ID was provied in the CompileContext call
compiledContexts = []
compiledContexts.append(compiledResp.context)

# Now we can send a recoginze request along with the compiled context. The
# context data is provided through the recognition config as a list of compiled
# contexts (i.e we can provide more than one compiled context if the model
# supports more than one context token).
cfg = cubic.RecognitionConfig(
    model_id=model.id,
    audio_encoding="WAV",
    context=cubic.RecognitionContext(compiled=compiledContexts),
)

# The rest is the same as a usual streaming recognize request

# open audio file stream
audio = open('test.wav', 'rb')

# send streaming request to cubic and print out results as they come in
for resp in client.StreamingRecognize(cfg, audio):
    for result in resp.results:
        if result.is_partial:
            print("\r{0}".format(result.alternatives[0].transcript), end="")
        else:
            print("\r{0}".format(result.alternatives[0].transcript), end="\n")

```

{{% /tab %}}

{{% tab "C#" %}}

``` c#
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Grpc.Core;

namespace CubicRecognitionContextExample {
    class Program {
        static async Task Main(string[] args) {

            // set creds = new Grpc.Core.SslCredentials(); if using TLS
            var serverAddress = "127.0.0.1:2727";
            var creds = Grpc.Core.ChannelCredentials.Insecure;

            // Initialize a gRPC connection
            var channel = new Grpc.Core.Channel(serverAddress, creds);
            var client = new CobaltSpeech.Cubic.Cubic.CubicClient(channel);

            // Get list of available models
            var listModelsRequest = new CobaltSpeech.Cubic.ListModelsRequest();
            var modelResp = client.ListModels(listModelsRequest);

            foreach (var m in modelResp.Models) {
                Console.WriteLine("\nID = {0}, Name = {1}, Supports Context = {2}",
                    m.Id, m.Name, m.Attributes.SupportsContext);
                // printing allowed context tokens
                if (m.Attributes.SupportsContext) {
                    Console.WriteLine("Allowed Context Tokens = {0}\n",
                        m.Attributes.AllowedContextTokens);
                }
            }

            // Assuming the first model supports context
            var model = modelResp.Models[0];

            // Let's say this model has an allowed context token called "airport_names" and
            // we have a list of airport names that we want to make sure the recognizer gets
            // right. We can compile the list of names into a easy to search and decodable
            // format for the model using the CompileContext() method. We can save the compiled
            // data and send it back during a recognize request to aid the speech recognition.

            // a small example list
            string[] phrases = { "NARITA", "KUALA LUMPUR INTERNATIONAL", "ISTANBUL ATATURK", "LAGUARDIA" };
            string contextToken = model.Attributes.AllowedContextTokens[0]; // "airport_names"

            // create compile context request
            var compileRequest = new CobaltSpeech.Cubic.CompileContextRequest {
                ModelId = model.Id,
                Token = contextToken,
            };
            // put phrases into an compileRequest.Phrases
            foreach (var phrase in phrases) {
                compileRequest.Phrases.Add(
                    new CobaltSpeech.Cubic.ContextPhrase {
                        Text = phrase,
                    });
            }

            // send request to server
            var compiledResp = client.CompileContext(compileRequest);

            // saving the compiled result for later use; note this compiled data is only
            // compatible with the model whose ID was provied in the CompileContext call
            var compiledContexts = new List<CobaltSpeech.Cubic.CompiledContext>();
            compiledContexts.Add(compiledResp.Context);

            // Now we can send a recoginze request along with the compiled context. The
            // context data is provided through the recognition config as a list of compiled
            // contexts (i.e we can provide more than one compiled context if the model
            // supports more than one context token).
            var cfg = new CobaltSpeech.Cubic.RecognitionConfig {
                ModelId = model.Id,
                AudioEncoding = CobaltSpeech.Cubic.RecognitionConfig.Types.Encoding.Wav,
                Context = new CobaltSpeech.Cubic.RecognitionContext(),
            };
            foreach (var ctx in compiledContexts) {
                cfg.Context.Compiled.Add(ctx);
            }

            // The rest is the same as a usual streaming recognize request

            string audioPath = "test.wav";

            // Setup the bi-directional gRPC stream.
            var call = client.StreamingRecognize();
            using(call) {
                // Setup recieve task
                var responseReaderTask = Task.Run(async() => {
                    // Wait for the next response
                    while (await call.ResponseStream.MoveNext()) {
                        var response = call.ResponseStream.Current;
                        foreach (var result in response.Results) {
                            Console.WriteLine(result.Alternatives[0].Transcript);
                        }
                    }
                });

                // Send config first, followed by the audio
                {
                    // Send the configs
                    var request = new CobaltSpeech.Cubic.StreamingRecognizeRequest();
                    request.Config = cfg;
                    await call.RequestStream.WriteAsync(request);

                    // Setup object for streaming audio
                    request.Config = null;
                    request.Audio = new CobaltSpeech.Cubic.RecognitionAudio { };

                    // Send the audio, in 8kb chunks
                    const int chunkSize = 8192;
                    using(FileStream file = File.OpenRead(audioPath)) {
                        int bytesRead;
                        var buffer = new byte[chunkSize];
                        while ((bytesRead = file.Read(buffer, 0, buffer.Length)) > 0) {
                            var bytes = Google.Protobuf.ByteString.CopyFrom(buffer.Take(bytesRead).ToArray());
                            request.Audio.Data = bytes;
                            await call.RequestStream.WriteAsync(request);
                        }

                        // Close the sending stream
                        await call.RequestStream.CompleteAsync();
                    }
                }

                // Wait for all of the responses to come back through the receiving stream
                await responseReaderTask;
            }
        }
    }
}

}
```

{{% /tab %}}

{{%/tabs %}}
