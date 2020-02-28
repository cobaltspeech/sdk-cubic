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

{{% tab "C#" %}}
``` c#
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
                var bytes = Google.Protobuf.ByteString.CopyFrom(buffer);
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

{{% tab "C#" %}}

We do not currently have example C# code for streaming from a microphone.
Simply pass the bytes from the microphone the same as is done from the file in the `Streaming from an audio file` example above.

{{% /tab %}}

{{% tab "Java/Android" %}}

This example uses the `android.media.AudioRecord` class and assumes the min API level is higher than Marshmallow.
Please note: this example does not attempt to handle threading and all exceptions.
It gives a simplified overview of the essential gRPC calls.

For a complete android example, see the examples directory in [the sdk-cubic github repository](https://github.com/cobaltspeech/sdk-cubic/examples/android).

``` java
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
```
{{% /tab %}}

{{% tab "Swift/iOS" %}}

This example uses CubicManager class and its CubicManagerDelegate protocol as a set of instruments for establishing connection to Cubic server, listing models, recording audio stream and performing rrecognition requests. You can use this class in your client view controller or any other object. See the CubicClient class example usage below.

``` swift

import AVFoundation
import SwiftProtobuf
import swift_cubic
import GRPC
import NIO

public protocol CubicManagerDelegate: class {
    
    func managerDidRecognizeWithResponse(_ res: Cobaltspeech_Cubic_RecognitionResponse)
    func streamCompletion(_ result: Cobaltspeech_Cubic_RecognitionResponse?)
    func streamReceive(_ result: Cobaltspeech_Cubic_RecognitionResponse)
    
}

public class CubicManager: NSObject, AVAudioRecorderDelegate {
    
    private let client: Cobaltspeech_Cubic_CubicServiceClient
    private var recorder: AVAudioRecorder!
    private var selectedModelId: String = "\(1)"
    private var callStream: BidirectionalStreamingCall<Cobaltspeech_Cubic_StreamingRecognizeRequest, Cobaltspeech_Cubic_RecognitionResponse>?
    private var audioEngine : AVAudioEngine!
    private var outref: ExtAudioFileRef?
    private var eventLoopGroup: EventLoopGroup
    public weak var delegate: CubicManagerDelegate?
    var isRecording = false
    
    let settings: [String : Any] = [
        AVFormatIDKey: Int(kAudioFormatLinearPCM),
        AVSampleRateKey: 16000.0,
        AVNumberOfChannelsKey: 1,
        AVLinearPCMBitDepthKey: 16,
        AVLinearPCMIsFloatKey: false,
        AVLinearPCMIsBigEndianKey: false,
        AVEncoderAudioQualityKey: AVAudioQuality.high.rawValue
    ]
    
    public var selectedModel: Cobaltspeech_Cubic_Model? {
        didSet {
            if let c = selectedModel {
                selectedModelId = c.id
            }
        }
    }
    
    public required init(client: Cobaltspeech_Cubic_CubicServiceClient) {
        self.client = client
        self.eventLoopGroup = PlatformSupport.makeEventLoopGroup(loopCount: 1,
                                                                 networkPreference: .best)
    }
    
    public init(host: String, port: Int, useTLS: Bool) {
        let target = ConnectionTarget.hostAndPort(host, port)
        self.eventLoopGroup = PlatformSupport.makeEventLoopGroup(loopCount: 1,
                                                                 networkPreference: .best)
        
        let tls = useTLS ? ClientConnection.Configuration.TLS() : nil
        
        let configuration = ClientConnection.Configuration(target: target, 
                                                           eventLoopGroup: self.eventLoopGroup, 
                                                           errorDelegate: nil,
                                                           connectivityStateDelegate: nil, 
                                                           tls: tls, 
                                                           connectionBackoff: nil)
        let connection = ClientConnection.init(configuration: configuration)
        self.client = Cobaltspeech_Cubic_CubicServiceClient(connection: connection)   
    }
    
    public func listModels(callback: @escaping(_ models: [Cobaltspeech_Cubic_Model]?, _ errorMessage: String?) -> ()) {
        let listModels = Cobaltspeech_Cubic_ListModelsRequest()
        
        client.listModels(listModels).response.whenComplete({ (result) in
            DispatchQueue.main.async {
                do {
                    let response = try result.get()
                    callback(response.models, nil)
                } catch let error {
                    print(error.localizedDescription)
                    callback(nil, error.localizedDescription)
                }
            }
        })
    }
    
    public func isAuthorized() -> Bool {
        return AVCaptureDevice.authorizationStatus(for: AVMediaType.audio) == .authorized
    }
    
    public func requestAccess(completionHandler: @escaping((_ granted: Bool) -> ())) {
        AVCaptureDevice.requestAccess(for:  AVMediaType.audio, completionHandler: completionHandler)
    }
    
    private class func getDocumentsDirectory() -> URL {
       let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
       let documentsDirectory = paths[0]
       return documentsDirectory
    }
    
    private class func getWavURL(_ name: String = "") -> URL {
        return CubicManager.getDocumentsDirectory().appendingPathComponent("records\(name).wav")
    }
    
    public func startStream() {
        let call = self.client.streamingRecognize(handler: { (response) in
            self.delegate?.streamCompletion(response)
        })

        var request = Cobaltspeech_Cubic_StreamingRecognizeRequest()
        request.config.modelID = self.selectedModelId
        request.config.idleTimeout.seconds = 5
        request.config.audioEncoding = .rawLinear16
        call.sendMessage(request).whenComplete({ (result) in
            do {
                try result.get()
            } catch let error {
                print(error.localizedDescription)
            }
        })
        
        callStream = call
        self.startAudioEngine()
    }
    
    func startAudioEngine() {
        try! AVAudioSession.sharedInstance().setCategory(.playAndRecord)
        try! AVAudioSession.sharedInstance().setActive(true)

        if audioEngine == nil {
            audioEngine = AVAudioEngine()
        }

        let format = AVAudioFormat(commonFormat: AVAudioCommonFormat.pcmFormatInt16,
                                   sampleRate: 44100.0,
                                   channels: 1,
                                   interleaved: true)
        
        let downFormat = AVAudioFormat(settings: settings)
        audioEngine.connect(audioEngine.inputNode, to: audioEngine.mainMixerNode, format: format)

        audioEngine.inputNode.installTap(onBus: 0,
                                         bufferSize: AVAudioFrameCount(format!.sampleRate * 0.4),
                                         format: format,
                                         block: { (buffer: AVAudioPCMBuffer!, time: AVAudioTime!) -> Void in
            let converter = AVAudioConverter(from: format!, to: downFormat!)
            let newBuffer = AVAudioPCMBuffer(pcmFormat: downFormat!,
                frameCapacity: AVAudioFrameCount(downFormat!.sampleRate * 0.4))
                                            
            let inputBlock: AVAudioConverterInputBlock = { (inNumPackets, outStatus) -> AVAudioBuffer? in
                outStatus.pointee = AVAudioConverterInputStatus.haveData
                let audioBuffer : AVAudioBuffer = buffer
                return audioBuffer
            }
                                            
            if let newBuffer = newBuffer {
                var error: NSError?
                converter!.convert(to: newBuffer, error: &error, withInputFrom: inputBlock)
                self.sendStreamPartToServer(newBuffer,time)
                _ = ExtAudioFileWrite(self.outref!, newBuffer.frameLength, newBuffer.audioBufferList)
            }
        })

        try! audioEngine.start()
        self.isRecording = true
    }
    
    private func sendStreamPartToServer(_ buffer: AVAudioPCMBuffer, _ time: AVAudioTime) {
        if let oldCall = self.callStream {
            var req = Cobaltspeech_Cubic_StreamingRecognizeRequest()
            let data = Data(buffer: buffer, time: time)
            req.audio.data = data
            oldCall.sendMessage(req).whenComplete({ (result) in
                do {
                    try result.get()
                } catch let error {
                    print(error.localizedDescription)
                }
            })
        }
    }
    
    func stopStream() {
        stopAudioEngine()
        stopGRPCStream()
    }
    
    private func stopAudioEngine() {
        if audioEngine != nil && audioEngine.isRunning {
            audioEngine.stop()
            audioEngine.inputNode.removeTap(onBus: 0)
            if let outref = outref {
                ExtAudioFileDispose(outref)
            }
            try? AVAudioSession.sharedInstance().setActive(false)
            self.isRecording = false
        }
    }
    
    private func stopGRPCStream() {
        if let call = self.callStream {
            call.sendEnd().whenFailure { (error) in
                print(error.localizedDescription)
            }
        }
    }
    
    @discardableResult
    public func record() -> Bool {
        if !isAuthorized() {
            return false
        }
        
        do {
            let audioURL = CubicManager.getWavURL()
            recorder = try AVAudioRecorder(url: audioURL, settings: settings)
            recorder.delegate = self
            isRecording = recorder.record()
            return isRecording
        } catch {
            finishRecording(success: false)
        }
        
        return false
    }
    
    func finishRecording(success: Bool) {
        recorder?.stop()
        recorder = nil
    }
    
    public func stop() {
        if (isRecording) {
            finishRecording(success: true)
            uploadRecord()
        }
    }
    
    func uploadRecord() {
        do {
            var request = Cobaltspeech_Cubic_RecognizeRequest()
            request.config = Cobaltspeech_Cubic_RecognitionConfig()
            request.config.modelID = selectedModelId
            request.config.idleTimeout = Google_Protobuf_Duration()
            request.config.idleTimeout.seconds = 5
            request.config.audioEncoding = .rawLinear16
            
            request.audio = Cobaltspeech_Cubic_RecognitionAudio()
            let audioUrl = CubicManager.getWavURL()
            request.audio.data = try Data(contentsOf: audioUrl)
            client.recognize(request).response.whenComplete({ (response) in
                if let result = try? response.get() {
                    DispatchQueue.main.async {
                        self.delegate?.managerDidRecognizeWithResponse(result)
                    }
                }
            })
        } catch let error {
            print(error.localizedDescription)
        }
    }
}

extension Data {
    
    init(buffer: AVAudioPCMBuffer, time: AVAudioTime) {
        let audioBuffer = buffer.audioBufferList.pointee.mBuffers
        self.init(bytes: audioBuffer.mData!, count: Int(audioBuffer.mDataByteSize))
    }

}

class CubicClient: NSObject, CubicManagerDelegate {
    
    let serverAddress = "demo-cubic.cobaltspeech.com"
    let serverPort = 2727
    
    var cubicManager: CubicManager!
    
    override init() {
        super.init()
        cubicManager = CubicManager(host: serverAddress, port: serverPort, useTLS: false)
        cubicManager.delegate = self
        cubicManager.listModels { (models, errorDescription) in
            if let model = models?.first {
                self.cubicManager.selectedModel = model
            }
        }
    }
    
    func startRecording() {
        if cubicManager.isAuthorized() {
            self.cubicManager.record()
        } else {
            self.cubicManager.requestAccess { (granted) in
                if granted {
                    print("audio recording access granted")
                }
            }
        }
    }
    
    func endRecording() {
        cubicManager.stop()
    }
    
    func printResult(response: Cobaltspeech_Cubic_RecognitionResponse?) {
        var resultMessage = ""
        
        if let response = response {
            for result in response.results {
                if !resultMessage.isEmpty {
                    resultMessage = resultMessage + "\n"
                }
                
                if let firstAlternative = result.alternatives.first {
                    resultMessage = resultMessage + "\(firstAlternative.transcript)"
                }
            }
        }
        
        print(resultMessage)
    }
    
    func managerDidRecognizeWithResponse(_ res: Cobaltspeech_Cubic_RecognitionResponse) {
        printResult(response: res)
    }
    
    func streamCompletion(_ result: Cobaltspeech_Cubic_RecognitionResponse?) {

    }
    
    func streamReceive(_ result: Cobaltspeech_Cubic_RecognitionResponse) {
    }
    
}

```
{{% /tab %}}

{{%/tabs %}}
