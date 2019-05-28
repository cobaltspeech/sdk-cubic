---
title: "Streaming Recognition"
weight: 30
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

### Streaming from microphone

Streaming audio from microphone input typically needs us to interact with system
libraries. There are several options available, and although the examples here use
one, you may choose to use an alternative as long as the recording audio format is
chosen correctly.

{{%tabs %}}
{{% tab "Go" %}}

This example utilizes the portaudio [bindings](https://github.com/gordonklaus/portaudio) for Go
to stream audio from a microphone. To use this package, install [PortAudio](http://www.portaudio.com/)
development headers and libraries using an appropriate package manager for your system (e.g. `apt-get install portaudio19-dev` on Ubuntu, `brew install portaudio` for OSX, etc.) or build from [source](http://portaudio.com/docs/v19-doxydocs/tutorial_start.html).

``` go
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
```
{{% /tab %}}

{{% tab "Python" %}}

This example requires the [pyaudio](http://people.csail.mit.edu/hubert/pyaudio/) 
module to stream audio from a microphone. Instructions for installing pyaudio for 
different systems are available at the link. On most platforms, this is simply `pip install pyaudio`.

``` python
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
```
{{% /tab %}}

{{%/tabs %}}
