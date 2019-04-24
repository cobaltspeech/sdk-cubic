---
title: "Cubic Protobuf API Docs"
weight: 100
---




## cubic.proto





### Service: Cubic
Service that implements the Cobalt Cubic Speech Recognition API

| Method Name | Request Type | Response Type | Description |
| ----------- | ------------ | ------------- | ------------|
| Version | .google.protobuf.Empty | VersionResponse | Queries the Version of the Server |
| ListModels | ListModelsRequest | ListModelsResponse | Retrieves a list of available speech recognition models |
| Recognize | RecognizeRequest | RecognitionResponse | Performs synchronous speech recognition: receive results after all audio has been sent and processed. It is expected that this request be typically used for short audio content: less than a minute long. For longer content, the `StreamingRecognize` method should be preferred. |
| StreamingRecognize | StreamingRecognizeRequest | RecognitionResponse | Performs bidirectional streaming speech recognition. Receive results while sending audio. This method is only available via GRPC and not via HTTP+JSON. However, a web browser may use websockets to use this service. |

 <!-- end services -->



### Message: ConfusionNetworkArc
An Arc inside a Confusion Network Link


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| word | string |  | <p>Word in the recognized transcript</p> |
| confidence | double |  | <p>Confidence estimate between 0 and 1. A higher number represents a higher likelihood that the word was correctly recognized.</p> |







### Message: ConfusionNetworkLink
A Link inside a confusion network


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| start_time | google.protobuf.Duration |  | <p>Time offset relative to the beginning of audio received by the recognizer and corresponding to the start of this link</p> |
| duration | google.protobuf.Duration |  | <p>Duration of the current link in the confusion network</p> |
| arcs | ConfusionNetworkArc | repeated | <p>Arcs between this link</p> |







### Message: ListModelsRequest
The top-level message sent by the client for the `ListModels` method.


This message is empty and has no fields.






### Message: ListModelsResponse
The message returned to the client by the `ListModels` method.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| models | Model | repeated | <p>List of models available for use that match the request.</p> |







### Message: Model
Description of a Cubic Model


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| id | string |  | <p>Unique identifier of the model. This identifier is used to choose the model that should be used for recognition, and is specified in the `RecognitionConfig` message.</p> |
| name | string |  | <p>Model name. This is a concise name describing the model, and maybe presented to the end-user, for example, to help choose which model to use for their recognition task.</p> |
| attributes | ModelAttributes |  | <p>Model attributes</p> |







### Message: ModelAttributes
Attributes of a Cubic Model


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| sample_rate | uint32 |  | <p>Audio sample rate supported by the model</p> |







### Message: RecognitionAlternative
A recognition hypothesis


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| transcript | string |  | <p>Text representing the transcription of the words that the user spoke.</p> |
| confidence | double |  | <p>Confidence estimate between 0 and 1. A higher number represents a higher likelihood of the output being correct.</p> |
| words | WordInfo | repeated | <p>A list of word-specific information for each recognized word. This is available only if `enable_word_confidence` or `enable_word_time_offsets` was set to `true` in the `RecognitionConfig`.</p> |
| start_time | google.protobuf.Duration |  | <p>Time offset relative to the beginning of audio received by the recognizer and corresponding to the start of this utterance.</p> |
| duration | google.protobuf.Duration |  | <p>Duration of the current utterance in the spoken audio.</p> |







### Message: RecognitionAudio
Audio to be sent to the recognizer


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| data | bytes |  | <p></p> |







### Message: RecognitionConfig
Configuration for setting up a Recognizer


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| model_id | string |  | <p>Unique identifier of the model to use, as obtained from a `Model` message.</p> |
| audio_encoding | RecognitionConfig.Encoding |  | <p>Encoding of audio data sent/streamed through the `RecognitionAudio` messages. For encodings like WAV/MP3 that have headers, the headers are expected to be sent at the beginning of the stream, not in every `RecognitionAudio` message.</p><p>If not specified, the default encoding is RAW_LINEAR16.</p><p>Depending on how they are configured, server instances of this service may not support all the encodings enumerated above. They are always required to accept RAW_LINEAR16. If any other `Encoding` is specified, and it is not available on the server being used, the recognition request will result in an appropriate error message.</p> |
| idle_timeout | google.protobuf.Duration |  | <p>Idle Timeout of the created Recognizer. If no audio data is received by the recognizer for this duration, ongoing rpc calls will result in an error, the recognizer will be destroyed and thus more audio may not be sent to the same recognizer. The server may impose a limit on the maximum idle timeout that can be specified, and if the value in this message exceeds that serverside value, creating of the recognizer will fail with an error.</p> |
| enable_word_time_offsets | bool |  | <p>This is an optional field. If this is set to true, each result will include a list of words and the start time offset (timestamp) and the duration for each of those words. If set to `false`, no word-level timestamps will be returned. The default is `false`.</p> |
| enable_word_confidence | bool |  | <p>This is an optional field. If this is set to true, each result will include a list of words and the confidence for those words. If `false`, no word-level confidence information is returned. The default is `false`.</p> |
| enable_raw_transcript | bool |  | <p>This is an optional field. If this is set to true, the transcripts will be presented as raw output from the recognizer without any formatting rules applied. They will be in all UPPER CASE, numbers and other special entities would be presented as the spoken words. If set to `false`, formatting rules will be applied to all results. The default is `false`.</p><p>As an example, if the spoken utterance was `here are four words`: with this field set to `false`: "Here are 4 words" with this field set to 'true' : "HERE ARE FOUR WORDS"</p> |
| enable_confusion_network | bool |  | <p>This is an optional field. If this is set to true, the results will include a confusion network. If set to `false`, no confusion network will be returned. The default is `false`. If the model being used does not support a confusion network, results may be returned without a confusion network available. If this field is set to `true`, then `enable_raw_transcript` is also forced to be true.</p> |
| audio_channels | uint32 | repeated | <p>This is an optional field. If the audio has multiple channels, this field should be configured with the list of channel indices that should be transcribed. Channels are 0-indexed.</p><p>Example: `[0]` for a mono file, `[0, 1]` for a stereo file.</p><p>If this field is not set, a mono file will be assumed by default and only channel-0 will be transcribed even if the file actually has additional channels.</p><p>Channels that are present in the audio may be omitted, but it is an error to include a channel index in this field that is not present in the audio. Channels may be listed in any order but the same index may not be repeated in this list.</p><p>BAD: `[0, 2]` for a stereo file; BAD: `[0, 0]` for a mono file.</p> |







### Message: RecognitionConfusionNetwork
Confusion network in recognition output


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| links | ConfusionNetworkLink | repeated | <p></p> |







### Message: RecognitionResponse
Collection of sequence of recognition results in a portion of audio.  When
transcribing a single audio channel (e.g. RAW_LINEAR16 input, or a mono
file), results will be ordered chronologically.  When transcribing multiple
channels, the results of all channels will be interleaved.  Results of each
individual channel will be chronological.  No such promise is made for the
ordering of results of different channels, as results are returned for each
channel individually as soon as they are ready.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| results | RecognitionResult | repeated | <p></p> |







### Message: RecognitionResult
A recognition result corresponding to a portion of audio.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| alternatives | RecognitionAlternative | repeated | <p>An n-best list of recognition hypotheses alternatives</p> |
| is_partial | bool |  | <p>If this is set to true, it denotes that the result is an interim partial result, and could change after more audio is processed. If unset, or set to false, it denotes that this is a final result and will not change.</p><p>Servers are not required to implement support for returning partial results, and clients should generally not depend on their availability.</p> |
| cnet | RecognitionConfusionNetwork |  | <p>If `enable_confusion_network` was set to true in the `RecognitionConfig`, and if the model supports it, a confusion network will be available in the results.</p> |
| audio_channel | uint32 |  | <p>Channel of the audio file that this result was transcribed from. For a mono file, or RAW_LINEAR16 input, this will be set to 0.</p> |







### Message: RecognizeRequest
The top-level message sent by the client for the `Recognize` method.  Both
the `RecognitionConfig` and `RecognitionAudio` fields are required.  The
entire audio data must be sent in one request.  If your audio data is larger,
please use the `StreamingRecognize` call..


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| config | RecognitionConfig |  | <p>Provides configuration to create the recognizer.</p> |
| audio | RecognitionAudio |  | <p>The audio data to be recognized</p> |







### Message: StreamingRecognizeRequest
The top-level message sent by the client for the `StreamingRecognize`
request.  Multiple `StreamingRecognizeRequest` messages are sent. The first
message must contain a `RecognitionConfig` message only, and all subsequent
messages must contain `RecognitionAudio` only.  All `RecognitionAudio`
messages must contain non-empty audio.  If audio content is empty, the server
may interpret it as end of stream and stop accepting any further messages.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| config | RecognitionConfig |  | <p></p> |
| audio | RecognitionAudio |  | <p></p> |







### Message: VersionResponse
The message sent by the server for the `Version` method.


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| cubic | string |  | <p>version of the cubic library handling the recognition</p> |
| server | string |  | <p>version of the server handling these requests</p> |







### Message: WordInfo
Word-specific information for recognized words


| Field | Type | Label | Description |
| ----- | ---- | ----- | ----------- |
| word | string |  | <p>The actual word in the text</p> |
| confidence | double |  | <p>Confidence estimate between 0 and 1. A higher number represents a higher likelihood that the word was correctly recognized.</p> |
| start_time | google.protobuf.Duration |  | <p>Time offset relative to the beginning of audio received by the recognizer and corresponding to the start of this spoken word.</p> |
| duration | google.protobuf.Duration |  | <p>Duration of the current word in the spoken audio.</p> |





 <!-- end messages -->



### Enum: RecognitionConfig.Encoding
The encoding of the audio data to be sent for recognition.

For best results, the audio source should be captured and transmitted using
the RAW_LINEAR16 encoding.

| Name | Number | Description |
| ---- | ------ | ----------- |
| RAW_LINEAR16 | 0 | Raw (headerless) Uncompressed 16-bit signed little endian samples (linear PCM), single channel, sampled at the rate expected by the chosen `Model`. |
| WAV | 1 | WAV (data with RIFF headers), with data sampled at a rate equal to or higher than the sample rate expected by the chosen Model. |
| MP3 | 2 | MP3 data, sampled at a rate equal to or higher than the sampling rate expected by the chosen Model. |
| FLAC | 3 | FLAC data, sampled at a rate equal to or higher than the sample rate expected by the chosen Model. |
| VOX8000 | 4 | VOX data (Dialogic ADPCM), sampled at 8 KHz. |
| ULAW8000 | 5 | μ-law (8-bit) encoded RAW data, single channel, sampled at 8 KHz. |


 <!-- end enums -->

 <!-- end HasExtensions -->




## Scalar Value Types

| .proto Type | Notes | Go Type | Python Type |
| ----------- | ----- | ------- | ----------- |
| double |  | float64 | float |
| float |  | float32 | float |
| int32 | Uses variable-length encoding. Inefficient for encoding negative numbers – if your field is likely to have negative values, use sint32 instead. | int32 | int |
| int64 | Uses variable-length encoding. Inefficient for encoding negative numbers – if your field is likely to have negative values, use sint64 instead. | int64 | int/long |
| uint32 | Uses variable-length encoding. | uint32 | int/long |
| uint64 | Uses variable-length encoding. | uint64 | int/long |
| sint32 | Uses variable-length encoding. Signed int value. These more efficiently encode negative numbers than regular int32s. | int32 | int |
| sint64 | Uses variable-length encoding. Signed int value. These more efficiently encode negative numbers than regular int64s. | int64 | int/long |
| fixed32 | Always four bytes. More efficient than uint32 if values are often greater than 2^28. | uint32 | int |
| fixed64 | Always eight bytes. More efficient than uint64 if values are often greater than 2^56. | uint64 | int/long |
| sfixed32 | Always four bytes. | int32 | int |
| sfixed64 | Always eight bytes. | int64 | int/long |
| bool |  | bool | boolean |
| string | A string must always contain UTF-8 encoded or 7-bit ASCII text. | string | str/unicode |
| bytes | May contain any arbitrary sequence of bytes. | []byte | str |


