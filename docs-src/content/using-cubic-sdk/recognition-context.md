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
allows the user to compile the list into a compact, efficient format for
passing to the `Recognize()` or `StreamingRecognize()` methods.

<!--more-->

### Compiling Recognition Context

We have several examples in different langagues below showing you how to compile
context data and send it during a recognition request.

{{< tabs >}}

{{< tab "Go" "go" >}}
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
            m.Id, m.Name, m.Attributes.ContextInfo.SupportsContext)
        // printing allowed context tokens
        if m.Attributes.ContextInfo.SupportsContext {
            fmt.Printf("Allowed Context Tokens = %v\n\n",
                strings.Join(m.Attributes.ContextInfo.AllowedContextTokens, ", "))
        }
    }

    // Assuming the first model supports context
    model := modelResp.Models[0]

    // Let's say this model has an allowed context token called "airport_names" and
    // we have a list of airport names that we want to make sure the recognizer gets
    // right. We compile the list of names using the CompileContext(), save the compiled
    // data and send it back with subsequent recognize requests to customize and improve the results.

    // a small example list
    phrases := []string{"NARITA", "KUALA LUMPUR INTERNATIONAL", "ISTANBUL ATATURK", "LAGUARDIA"}
    contextToken := model.Attributes.ContextInfo.AllowedContextTokens[0] // "airport_names"

    // sending request to server
    compiledResp, err := client.CompileContext(
        context.Background(), model.Id, contextToken, phrases, nil)
    if err != nil {
        log.Fatal(err)
    }

    // saving the compiled result for later use; note this compiled data is only
    // compatible with the model whose ID was provided in the CompileContext call
    compiledContexts := make([]*cubicpb.CompiledContext, 0)
    compiledContexts = append(compiledContexts, compiledResp.Context)

    // Now we can send a recognize request along with the compiled context. The
    // context data is provided through the recognition config as a list of compiled
    // contexts (i.e. we can provide more than one compiled context if the model
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
{{< /tab >}}

{{< tab "Python" "python" >}}
import cubic

# set insecure to False if server uses TLS
serverAddress = '127.0.0.1:2727'
client = cubic.Client(serverAddress, insecure=True)

# Get list of available models
modelResp = client.ListModels()
for m in modelResp.models:
    print("\nID = {}, Name = {}, Supports Context = {}".format(
        m.id, m.name, m.attributes.context_info.supports_context))
    if m.attributes.context_info.supports_context:
        # printing allowed context tokens
        print("Allowed context tokens =  {}\n".format(
            str(m.attributes.context_info.allowed_context_tokens)))

# Assuming the first model supports context
model = modelResp.models[0]

# Let's say this model has an allowed context token called "airport_names" and
# we have a list of airport names that we want to make sure the recognizer gets
# right. We compile the list of names using the CompileContext(), save the compiled
# data and send it back with subsequent recognize requests to customize and improve the results.

# a small example list
phrases = ["NARITA", "KUALA LUMPUR INTERNATIONAL", "ISTANBUL ATATURK", "LAGUARDIA"]
contextToken = model.attributes.context_info.allowed_context_tokens[0]  # "airport_names"

# sending request to server
compiledResp = client.CompileContext(model.id, contextToken, phrases)

# saving the compiled result for later use; note this compiled data is only
# compatible with the model whose ID was provided in the CompileContext call
compiledContexts = []
compiledContexts.append(compiledResp.context)

# Now we can send a recognize request along with the compiled context. The
# context data is provided through the recognition config as a list of compiled
# contexts (i.e. we can provide more than one compiled context if the model
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
{{< /tab >}}

{{< tab "C++" "c++" >}}
#include "cubic_client.h"

#include <iostream>
#include <fstream>
#include <string>
#include <thread>
#include <vector>

namespace CubicPB = cobaltspeech::cubic;

const std::string serverAddress = "localhost:2727";
const std::string filename = "test.wav";

int main(int argc, char *argv[]) {
    // Create the client (note this is an insecure connection,
    // which is not recommended for production).
    CubicClient client(serverAddress);

    // Get the list of available models.
    std::vector<CubicModel> models = client.listModels();
    std::cout << "Available Models:" << std::endl;
    for (const CubicModel &m : models) {
        std::cout << "  ID = " << m.id() << std::endl
                    << "  Name = " << m.name()
                    << "  Supports Context = " << m.supportsContext() << std::endl;
        
        if (m.supportsContext()) {
            std::vector<std::string> contextTokens = m.allowedContextTokens();
            std::string tokString;
            for (const std::string &tok : contextTokens) {
                tokString += tok + ", ";
            }
            tokString.erase(tokString.end() - 2, tokString.end());

            std::cout << "  Allowed Context Tokens = " << tokString << std::endl;
        }

        std::cout << std::endl;
    }

    // Assuming the first model supports context.
    auto model = models[0];

    // Let's say this model has an allowed context token called
    // "airport_names" and we have a list of airport names that
    // we want to make sure the recognizer gets right. We compile
    // the list of names using the CompileContext() method, save
    // the compiled data and send it with subsequent recognize
    // requests to customize and improve the results.
    std::vector<std::string> phrases {
        "NARITA",
        "KUALA LUMPUR INTERNATIONAL",
        "ISTANBUL ATATURK",
        "LAGUARDIA"
    };

    std::string contextToken = model.allowedContextTokens()[0]; // "airport_names"
    CubicPB::CompiledContext compiledResp =
        client.compileContext(model.id(), contextToken, phrases);

    // Save the compiled result for later use. Note this compiled
    // data is only compatible with the model ID used to create it.
    CubicPB::RecognitionContext compiledContexts;
    *(compiledContexts.add_compiled()) = compiledResp;

    // Now we can send a recognize request along with the compiled
    // context. The context data is provided throught he recognition
    // config.
    CubicPB::RecognitionConfig cfg;
    cfg.set_model_id(model.id());
    cfg.set_audio_encoding(CubicPB::RecognitionConfig::WAV);
    *(cfg.mutable_context()) = compiledContexts;

    // The rest is the same as the usual streaming recognize request.

    // Create the stream.
    auto stream = client.streamingRecognize(cfg);

    // Push the audio on a separate thread.
    std::thread audioThread([&stream](){
        // Open the file and push audio bytes
        std::ifstream infile(filename);
        std::streamsize buffSize = 8192;
        char *buff = new char[buffSize];
        while (infile.good()) {
            infile.read(buff, buffSize);
            stream.pushAudio(buff, infile.gcount());
        }

        // Let Cubic know that no more audio will be coming.
        stream.audioFinished();
        delete[] buff;
    });

    // Print the results as they come.
    CubicPB::RecognitionResponse resp;
    while (stream.receiveResults(&resp)) {
        for (int i = 0; i < resp.results_size(); i++) {
            CubicPB::RecognitionResult result = resp.results(i);
            if (!result.is_partial()) {
                std::cout << result.alternatives(0).transcript() << std::endl;
            }
        }
    }

    // Close the stream.
    audioThread.join();
    stream.close();
}
{{< /tab >}}

{{< tab "C#" "c#" >}}
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
                    m.Id, m.Name, m.Attributes.ContextInfo.SupportsContext);
                // printing allowed context tokens
                if (m.Attributes.ContextInfo.SupportsContext) {
                    Console.WriteLine("Allowed Context Tokens = {0}\n",
                        m.Attributes.ContextInfo.AllowedContextTokens);
                }
            }

            // Assuming the first model supports context
            var model = modelResp.Models[0];

            // Let's say this model has an allowed context token called "airport_names" and
            // we have a list of airport names that we want to make sure the recognizer gets
            // right. We compile the list of names using the CompileContext(), save the compiled
            // data and send it back with subsequent recognize requests to customize and improve the results.

            // a small example list
            string[] phrases = { "NARITA", "KUALA LUMPUR INTERNATIONAL", "ISTANBUL ATATURK", "LAGUARDIA" };
            string contextToken = model.Attributes.ContextInfo.AllowedContextTokens[0]; // "airport_names"

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
            // compatible with the model whose ID was provided in the CompileContext call
            var compiledContexts = new List<CobaltSpeech.Cubic.CompiledContext>();
            compiledContexts.Add(compiledResp.Context);

            // Now we can send a recognize request along with the compiled context. The
            // context data is provided through the recognition config as a list of compiled
            // contexts (i.e. we can provide more than one compiled context if the model
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
{{< /tab >}}

{{< tab "Swift/iOS" "swift" >}}
import Foundation
import Cubic

class CubicExample {

    // set useTLS to true if using TLS
    let client = Client(host: "127.0.0.1", port: 2727, useTLS: false)
    var config = Cobaltspeech_Cubic_RecognitionConfig()
    let fileName = "test.wav"
    let chunkSize = 8192
    
    public init() {
        let dispatchGroup = DispatchGroup()
        dispatchGroup.enter()
        
        var model: Cobaltspeech_Cubic_Model!

        client.listModels(success: { (models) in
            guard let models = models else { return }
            
            for model in models {
                print("\nID = \(model.id), Name = \(model.name), Supports Context = \(model.attributes.contextInfo.supportsContext)")
                // printing allowed context tokens
                if model.attributes.contextInfo.supportsContext {
                    print("Allowed Context Tokens = \(model.attributes.contextInfo.allowedContextTokens)\n")
                }
            }
            
            // Assuming the first model supports context
            if let firstModel = models.first {
                model = firstModel
            } else {
                return
            }
            
            dispatchGroup.leave()
        }) { (error) in
            print(error.localizedDescription)
            dispatchGroup.leave()
        }
        
        dispatchGroup.wait()
        
        // Let's say this model has an allowed context token called "airport_names" and
        // we have a list of airport names that we want to make sure the recognizer gets
        // right. We compile the list of names using the CompileContext(), save the compiled
        // data and send it back with subsequent recognize requests to customize and improve the results.
        
        // a small example list
        let phrases = ["NARITA", "KUALA LUMPUR INTERNATIONAL", "ISTANBUL ATATURK", "LAGUARDIA"]
        let contextToken = model.attributes.contextInfo.allowedContextTokens[0] // "airport_names"
        
        // create compile context request
        var compileRequest = Cobaltspeech_Cubic_CompileContextRequest()
        compileRequest.modelID = model.id
        compileRequest.token = contextToken
        
        // put phrases into an compileRequest.Phrases
        for phrase in phrases {
            var contextPhrase = Cobaltspeech_Cubic_ContextPhrase()
            contextPhrase.text = phrase
            compileRequest.phrases.append(contextPhrase)
        }

        // send request to server
        var compiledContexts: [Cobaltspeech_Cubic_CompiledContext] = []
        
        dispatchGroup.enter()
        
        self.client.compileContext(compileRequest).response.whenComplete { (result) in
            switch result {
            case .success(let response):
                // saving the compiled result for later use; note this compiled data is only
                // compatible with the model whose ID was provided in the CompileContext call
                compiledContexts.append(response.context)
                dispatchGroup.leave()
            case .failure(let error):
                print(error.localizedDescription)
                dispatchGroup.leave()
            }
        }
        
        dispatchGroup.wait()
        
        // Now we can send a recognize request along with the compiled context. The
        // context data is provided through the recognition config as a list of compiled
        // contexts (i.e. we can provide more than one compiled context if the model
        // supports more than one context token).
        self.config.modelID = model.id
        self.config.audioEncoding = .wav
        self.config.context = Cobaltspeech_Cubic_RecognitionContext()
        self.config.context.compiled.append(contentsOf: compiledContexts)

        // The rest is the same as a usual streaming recognize request
        let fileUrl = URL(fileURLWithPath: self.fileName)
        guard let audioData = try? Data(contentsOf: fileUrl) else { return }
        
        dispatchGroup.enter()
        
        self.client.streamingRecognize(audioData: audioData, chunkSize: self.chunkSize, config: self.config, success: { (response) in
            for result in response.results {
                if !result.isPartial, let alternative = result.alternatives.first {
                    print(alternative.transcript)
                }
            }
            
            dispatchGroup.leave()
        }) { (error) in
            print(error.localizedDescription)
            dispatchGroup.leave()
        }
    }
}
{{< /tab >}}

{{< tab "NodeJS" "js" >}}
import {RecognitionAudio,
        RecognitionConfig, 
        ListModelsRequest, 
        RecognizeRequest } from '@cobaltspeech/sdk-cubic';
    const serverAddr = "127.0.0.1:2727"
    let client =new CubicClient(serverAddr)
let listRequest =new ListModelsRequest()
client.listModels(listRequest,(err,response)=>{
    if (err){
        console.error(err)
        return
    }
    let models = response.getModelsList()
    for(let model of models){
        console.log(`${model.getId()} ${model.getName()} ${model.getAttributes()}`)
    }
    if (models.length>0){
        let firstModel = models[0]
        let phrases = ["NARITA", "KUALA LUMPUR INTERNATIONAL", "ISTANBUL ATATURK", "LAGUARDIA"].map(a=>{
            let ctx = new ContextPhrase()
            ctx.setText(a)
            return ctx
        })

        let contextToken = firstModel.getAttributes().getContextInfo().addAllowedContextTokens[0] // "airport_names"
      
        // sending request to server
        let completeContext = new CompileContextRequest()
        completeContext.setModelId(firstModel.getId())
        completeContext.setPhrasesList(phrases)
        completeContext.setToken(contextToken)

        client.compileContext(completeContext,(err,response)=>{
            if (err!=null) {
                console.error(err)
                return
            }

            let stream = client.streamingRecognize()
            stream.on('data',(response)=>{
                let results = response.getResultsList()
                for (let result of results){
                    let alternatives = result.getAlternativesList()
                    if (alternatives.length>0){
                        let words = alternatives[0].getWordsList()
                        for (let word of words){
                            console.log(word.getWord())
                        }
                    }
                }
            })

            let config = new RecognitionConfig()
            config.addAudioChannels(1)
            config.setModelId('1');
            let recContext =new RecognitionContext()
            let completedContext = response.getContext()
            recContext.setCompiledList([completedContext])
            config.setContext()
            let req = new StreamingRecognizeRequest()
            req.setConfig(config)
            stream.write(req)

            for (let chain of audioChunks){
                req = new StreamingRecognizeRequest()
                let reqAudio =new RecognitionAudio()
                reqAudio.setData(chain)
                req.setAudio(reqAudio)
                stream.write(req)
            }
        })
    }
})
{{< /tab>}}

{{< /tabs >}}
