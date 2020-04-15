---
title: "Streaming Recognition"
weight: 24
---

The following example shows how to transcribe an audio file using Cubic’s
Streaming Recognize Request. The stream can come from a file on disk or be
directly from a microphone in real time.

<!--more-->

### Streaming from an audio file

We support several file formats including WAV, MP3, FLAC etc. For more details, please
see the protocol buffer specification file in the SDK repository (`grpc/cubic.proto`).
The examples below use a WAV file as input to the streaming recognition. We will query
the server for available models and use the first model to transcribe the speech.

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

	// Define a callback function to handle results
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

{{< /tab >}}

{{< tab "C#" "c#" >}}
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using Grpc.Core;

// Initialize a gRPC connection
var creds = Grpc.Core.ChannelCredentials.Insecure;
var channel = new Grpc.Core.Channel(url, creds);
var client = new CobaltSpeech.Cubic.Cubic.CubicClient(channel);

// List the available models
var listModelsRequest = new CobaltSpeech.Cubic.ListModelsRequest();
var models = client.ListModels(listModelsRequest);

// Setup the bi-directional gRPC stream.
var call = client.StreamingRecognize();
using (call)
{
    // Setup recieve task
    var responseReaderTask = Task.Run(async () =>
    {
        // Wait for the next response
        while (await call.ResponseStream.MoveNext())
        {
            var response = call.ResponseStream.Current;
            foreach (var result in response.Results)
            {
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
        using (FileStream file = File.OpenRead("test.raw"))
        {
            int bytesRead;
            var buffer = new byte[chunkSize];
            while ((bytesRead = file.Read(buffer, 0, buffer.Length)) > 0)
            {
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

public static void transcribeFile() {
    // Setup connection
    CubicGrpc.CubicStub mCubicService = CubicGrpc.newStub(
        ManagedChannelBuilder.forTarget(url).build());

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

    // Setup bidirectional stream
    StreamObserver<StreamingRecognizeRequest> requestObserver; // Outgoing messages are sent on this request object
    requestObserver = mCubicService.streamingRecognize(mRecognitionResponseObserver);

    // Send config message
    StreamingRecognizeRequest configs = StreamingRecognizeRequest.newBuilder()
        // Note, we do not call setAudio here.
        .setConfig(RecognitionConfig.newBuilder()
            .setModelId("ModelID")
            .build())
        .build();
    requestObserver.onNext(configs);

    // Read the file in chunks and stream to server.
    try {
        FileInputStream is = new FileInputStream(new File("/path/to/file"));
        byte[] bytes = new byte[1024];
        int len = 0;

        // Read the file
        while ((len = is.read(chunk)) != -1) {
            // Convert byte[] to ByteString for gRPC
            ByteString audioBS = ByteString.copyFrom(chunk);

            // Send audio to server
            RecognitionAudio audioMsg = RecognitionAudio.newBuilder()
                .setData(audioBS)
                .build()
            requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
                .setAudio(audioMsg)
                .build());
        }
    } catch (Exception e) { } // Handle exception

    // Close the client side stream
    requestObserver.onCompleted();

    // Note: Once the server is done transcribing everything, responseObserver.onCompleted() will be called.
}
{{< /tab >}}

{{< /tabs >}}

### Streaming from microphone

Streaming audio from microphone input typically needs us to interact with system
libraries. There are several options available, and although the examples here use
one, you may choose to use an alternative as long as the recording audio format is
chosen correctly.

{{< tabs >}}
{{< tab "Go" "go" >}}

/*
This example utilizes the portaudio bindings for Go (see https://github.com/gordonklaus/portaudio)
to stream audio from a microphone. To use this package, install PortAudio (see http://www.portaudio.com/)
development headers and libraries using an appropriate package manager for your system (e.g. `apt-get install portaudio19-dev` on Ubuntu, `brew install portaudio` for OSX, etc.) or build from [source](http://portaudio.com/docs/v19-doxydocs/tutorial_start.html).

*/

package main

import (
	"bytes"
	"context"
	"encoding/binary"
	"fmt"
	"log"

	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic"
	"github.com/cobaltspeech/sdk-cubic/grpc/go-cubic/cubicpb"
	"github.com/gordonklaus/portaudio"
)

const serverAddr = "127.0.0.1:2727"

// Microphone implements the io.ReadCloser interface and provides
// a data stream for microphone input.
type Microphone struct {
	buffer []int16
	stream *portaudio.Stream
}

// NewMicrophone instantiates a Microphone object with the desired
// sampling rate and buffer size. When streaming to cubic, the sample
// rate should be set to the sample rate of the model used.
func NewMicrophone(sampleRate, bufferSize uint32) (*Microphone, error) {

	// bufferSize is measured in number of bytes. Since we are capturing
	// 16 bit audio, each sample is 2 bytes. The microphone has a int16
	// buffer, so we use the number of samples as its size.
	numSamples := bufferSize/2
	mic := Microphone{buffer: make([]int16, numSamples)}

	portaudio.Initialize()
	stream, err := portaudio.OpenDefaultStream(1, 0, float64(sampleRate), int(numSamples), mic.buffer)
	if err != nil {
		return nil, err
	}
	mic.stream = stream

	err = mic.stream.Start()
	if err != nil {
		return nil, err
	}

	return &mic, nil
}

// Read copies N bytes into the passed buffer from the microphone audio buffer
// where N is the buffer size passed to `cubic.NewClient`. Also, to be compatible
// with the cubic client, it returns two things : an int representing the number of
// bytes copied and an error.
func (mic *Microphone) Read(buffer []byte) (int, error) {
	err := mic.stream.Read()
	if err != nil {
		return 0, err
	}
	byteBuffer := new(bytes.Buffer)
	err = binary.Write(byteBuffer, binary.LittleEndian, mic.buffer)
	if err != nil {
		return 0, err
	}
	copy(buffer, byteBuffer.Bytes())
	return len(buffer), nil
}

// Close shuts the microphone stream down and cleans up
func (mic *Microphone) Close() {
	mic.stream.Stop()
	mic.stream.Close()
	portaudio.Terminate()
}

func main() {

	bufferSize := uint32(8192)
	client, err := cubic.NewClient(serverAddr, cubic.WithInsecure(), cubic.WithStreamingBufferSize(bufferSize))
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

	cfg := &cubicpb.RecognitionConfig{
		ModelId: model.Id,
	}

	// Define a callback function to handle results
	resultHandler := func(resp *cubicpb.RecognitionResponse) {
		for _, r := range resp.Results {
			if r.IsPartial {
				fmt.Print("\r", r.Alternatives[0].Transcript) // print on same line
			} else {
				fmt.Println("\r", r.Alternatives[0].Transcript) // print and move to new line
			}
		}
	}

	// Create microphone stream
	mic, err := NewMicrophone(model.Attributes.SampleRate, bufferSize)
	if err != nil {
		log.Fatal(err)
	}
	defer mic.Close()

	// Since Microphone implements the io.ReadCloser interface, we can
	// pass it directly to the StreamingRecognize function.
	err = client.StreamingRecognize(context.Background(), cfg, mic, resultHandler)
	if err != nil {
		log.Fatal(err)
	}
}
{{< /tab >}}

{{< tab "Python" "python" >}}
#
# This example requires the pyaudio (http://people.csail.mit.edu/hubert/pyaudio/)
# module to stream audio from a microphone. Instructions for installing pyaudio for
# different systems are available at the link. On most platforms, this is simply `pip install pyaudio`.

import cubic
import pyaudio

serverAddress = '127.0.0.1:2727'

client = cubic.Client(serverAddress)

# get list of available models
modelResp = client.ListModels()

# use the first available model
model = modelResp.models[0]

cfg = cubic.RecognitionConfig(
	model_id = model.id
)

# client.StreamingRecognize takes any binary stream object that has a read(nBytes)
# method. The method should return nBytes from the stream. So pyaudio is a suitable
# library to use here for streaming audio from the microphone. Other libraries or
# modules may also be used as long as they have the read method or have been wrapped
# to do so.

# open microphone stream
p = pyaudio.PyAudio()
audio = p.open(format=pyaudio.paInt16,              # 16 bit samples
				channels=1,                         # mono audio
				rate=model.attributes.sample_rate,  # sample rate in hertz
				input=True)                         # audio input stream

# send streaming request to cubic and
# print out results as they come in
try:
	for resp in client.StreamingRecognize(cfg, audio):
		for result in resp.results:
			if result.is_partial:
				print("\r{0}".format(result.alternatives[0].transcript), end="")
			else:
				print("\r{0}".format(result.alternatives[0].transcript), end="\n")
except KeyboardInterrupt:
	# stop streaming when ctrl+C pressed
	pass
except Exception as err:
	print("Error while trying to stream audio : {}".format(err))

audio.stop_stream()
audio.close()
{{< /tab >}}

{{< tab "C#" "c#" >}}

We do not currently have example C# code for streaming from a microphone.
Simply pass the bytes from the microphone the same as is done from the file in the `Streaming from an audio file` example above.

{{< /tab >}}

{{< tab "Java/Android" "java" >}}

/*
This example uses the `android.media.AudioRecord` class and assumes the min API level is higher than Marshmallow.
Please note: this example does not attempt to handle threading and all exceptions.
It gives a simplified overview of the essential gRPC calls.

For a complete android example, see the examples directory in [the sdk-cubic github repository](https://github.com/cobaltspeech/sdk-cubic/examples/android).
*/

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import com.google.protobuf.ByteString;

import com.cobaltspeech.cubic.CubicGrpc;
import com.cobaltspeech.cubic.CubicOuterClass.*;

public static void streamMicrophoneAudio() {
    // Setup connection
    CubicGrpc.CubicStub mCubicService = CubicGrpc.newStub(
        ManagedChannelBuilder.forTarget(url).build());

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

    // Setup bidirectional stream
    StreamObserver<StreamingRecognizeRequest> requestObserver; // Outgoing messages are sent on this request object
    requestObserver = mCubicService.streamingRecognize(mRecognitionResponseObserver);

    // Send config message
    RecognitionConfig cfg = RecognitionConfig.newBuilder()
            .setModelId("ModelID")
            .build();
    StreamingRecognizeRequest configs = StreamingRecognizeRequest.newBuilder()
        // Note, we do not call setAudio here.
        .setConfig(cfg)
        .build();
    requestObserver.onNext(configs);

    // Setup the Android Micorphone Recorder
    int SAMPLE_RATE = 8000; // Same as the model is expecting
    int BUFFER_SIZE = 1024;
    AudioRecord recorder = new AudioRecord(
        MediaRecorder.AudioSource.MIC,
        SAMPLE_RATE,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        BUFFER_SIZE);
    byte[] audioBuffer = new byte[BUFFER_SIZE];
    recorder.startRecording();

    // Read the file in chunks and stream to server.
    while (running) {
        recorder.read(audioBuffer, 0, BUFFER_SIZE, AudioRecord.READ_BLOCKING);

        // Convert byte[] to ByteString for gRPC
        ByteString audioBS = ByteString.copyFrom(audioBuffer);

        // Send audio to server
        RecognitionAudio audioMsg = RecognitionAudio.newBuilder()
            .setData(audioBS)
            .build();
        requestObserver.onNext(StreamingRecognizeRequest.newBuilder()
            .setAudio(audioMsg)
            .build());
    }

    // Stop the microphone recoding.
    recorder.stop();

    // Close the client side stream
    requestObserver.onCompleted();

    // Note: Once the server is done transcribing everything, it will call responseObserver.onCompleted().
}
{{< /tab >}}

{{< tab "Swift/iOS" "swift" >}}

/*
This example uses methods of the Client class to establish connection to Cubic server, list models, stream audio file and receive the transcription results.
You can call Client from your client view controller or any other class.
*/

import Cubic
import GRPC

class CubicExample {

    let client = Client(host: "demo-cubic.cobaltspeech.com", port: 2727, useTLS: true)
    var confg = Cobaltspeech_Cubic_RecognitionConfig()
    let fileName = "test.wav"
    let chunkSize = 8192

    public init() {
        let fileUrl = URL(fileURLWithPath: fileName)

        guard let audioData = try? Data(contentsOf: fileUrl) else { return }

        config.audioEncoding = .wav

        client.listModels(success: { (models) in
            if let model = models?.first {
                self.config.modelID = model.id

                self.client.streamingRecognize(audioData: audioData, chunkSize: self.chunkSize, config: self.config, success: { (response) in
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
