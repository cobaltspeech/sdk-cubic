/* eslint-disable */
import { util, configure, Writer, Reader } from 'protobufjs/minimal';
import * as Long from 'long';
import { grpc } from '@improbable-eng/grpc-web';
import { Observable } from 'rxjs';
import { Empty } from './google/protobuf/empty';
import { BrowserHeaders } from 'browser-headers';
import { share } from 'rxjs/operators';
import { Duration } from './google/protobuf/duration';

export const protobufPackage = 'cobaltspeech.cubic';

/** The top-level message sent by the client for the `ListModels` method. */
export interface ListModelsRequest {
}

/**
 * The top-level message sent by the client for the `Recognize` method.  Both
 * the `RecognitionConfig` and `RecognitionAudio` fields are required.  The
 * entire audio data must be sent in one request.  If your audio data is larger,
 * please use the `StreamingRecognize` call..
 */
export interface RecognizeRequest {
/** Provides configuration to create the recognizer. */
config: RecognitionConfig | undefined,
/** The audio data to be recognized */
audio: RecognitionAudio | undefined,
}

/**
 * The top-level message sent by the client for the `StreamingRecognize`
 * request.  Multiple `StreamingRecognizeRequest` messages are sent. The first
 * message must contain a `RecognitionConfig` message only, and all subsequent
 * messages must contain `RecognitionAudio` only.  All `RecognitionAudio`
 * messages must contain non-empty audio.  If audio content is empty, the server
 * may interpret it as end of stream and stop accepting any further messages.
 */
export interface StreamingRecognizeRequest {
config: RecognitionConfig | undefined,
audio: RecognitionAudio | undefined,
}

/**
 * The top-level message sent by the client for the `CompileContext` request. It
 * contains a list of phrases or words, paired with a context token included in
 * the model being used. The token specifies a category such as "menu_item",
 * "airport", "contact", "product_name" etc. The context token is used to
 * determine the places in the recognition output where the provided list of
 * phrases or words may appear. The allowed context tokens for a given model can
 * be found in its `ModelAttributes.ContextInfo` obtained via the `ListModels`
 * method.
 */
export interface CompileContextRequest {
/**
 * Unique identifier of the model to compile the context information for. The
 * model chosen needs to support context which can be verified by checking its
 * `ModelAttributes.ContextInfo` obtained via `ListModels`.
 */
modelId: string,
/**
 * The token that is associated with the provided list of phrases or words
 * (e.g "menu_item", "airport" etc.). Must be one of the tokens included in
 * the model being used, which can be retrieved by calling the `ListModels`
 * method.
 */
token: string,
/** List of phrases and/or words to be compiled. */
phrases: ContextPhrase[],
}

/** The message sent by the server for the `Version` method. */
export interface VersionResponse {
/** version of the cubic library handling the recognition */
cubic: string,
/** version of the server handling these requests */
server: string,
}

/** The message returned to the client by the `ListModels` method. */
export interface ListModelsResponse {
/** List of models available for use that match the request. */
models: Model[],
}

/**
 * Collection of sequence of recognition results in a portion of audio.  When
 * transcribing a single audio channel (e.g. RAW_LINEAR16 input, or a mono
 * file), results will be ordered chronologically.  When transcribing multiple
 * channels, the results of all channels will be interleaved.  Results of each
 * individual channel will be chronological.  No such promise is made for the
 * ordering of results of different channels, as results are returned for each
 * channel individually as soon as they are ready.
 */
export interface RecognitionResponse {
results: RecognitionResult[],
}

/** The message returned to the client by the `CompileContext` method. */
export interface CompileContextResponse {
/**
 * Context information in a compact form that is efficient for use in
 * subsequent recognition requests. The size of the compiled form will depend
 * on the amount of text that was sent for compilation. For 1000 words it's
 * generally less than 100 kilobytes.
 */
context: CompiledContext | undefined,
}

/** Configuration for setting up a Recognizer */
export interface RecognitionConfig {
/** Unique identifier of the model to use, as obtained from a `Model` message. */
modelId: string,
/**
 * Encoding of audio data sent/streamed through the `RecognitionAudio`
 * messages.  For encodings like WAV/MP3 that have headers, the headers are
 * expected to be sent at the beginning of the stream, not in every
 * `RecognitionAudio` message.
 * 
 * If not specified, the default encoding is RAW_LINEAR16.
 * 
 * Depending on how they are configured, server instances of this service may
 * not support all the encodings enumerated above. They are always required to
 * accept RAW_LINEAR16.  If any other `Encoding` is specified, and it is not
 * available on the server being used, the recognition request will result in
 * an appropriate error message.
 */
audioEncoding: RecognitionConfig_Encoding,
/**
 * Idle Timeout of the created Recognizer.  If no audio data is received by
 * the recognizer for this duration, ongoing rpc calls will result in an
 * error, the recognizer will be destroyed and thus more audio may not be sent
 * to the same recognizer.  The server may impose a limit on the maximum idle
 * timeout that can be specified, and if the value in this message exceeds
 * that serverside value, creating of the recognizer will fail with an error.
 */
idleTimeout: Duration | undefined,
/**
 * This is an optional field.  If this is set to true, each result will
 * include a list of words and the start time offset (timestamp) and the
 * duration for each of those words.  If set to `false`, no word-level
 * timestamps will be returned.  The default is `false`.
 */
enableWordTimeOffsets: boolean,
/**
 * This is an optional field.  If this is set to true, each result will
 * include a list of words and the confidence for those words.  If `false`, no
 * word-level confidence information is returned.  The default is `false`.
 */
enableWordConfidence: boolean,
/**
 * This is an optional field.  If this is set to true, the field
 * `RecognitionAlternative.raw_transcript` will be populated with the raw
 * transcripts output from the recognizer will be exposed without any
 * formatting rules applied.  If this is set to false, that field will not
 * be set in the results.  The RecognitionAlternative.transcript will
 * always be populated with text formatted according to the server's settings.
 */
enableRawTranscript: boolean,
/**
 * This is an optional field.  If this is set to true, the results will
 * include a confusion network.  If set to `false`, no confusion network will
 * be returned.  The default is `false`.  If the model being used does not
 * support a confusion network, results may be returned without a confusion
 * network available.  If this field is set to `true`, then
 * `enable_raw_transcript` is also forced to be true.
 */
enableConfusionNetwork: boolean,
/**
 * This is an optional field.  If the audio has multiple channels, this field
 * should be configured with the list of channel indices that should be
 * transcribed.  Channels are 0-indexed.
 * 
 * Example: `[0]` for a mono file, `[0, 1]` for a stereo file.
 * 
 * If this field is not set, a mono file will be assumed by default and only
 * channel-0 will be transcribed even if the file actually has additional
 * channels.
 * 
 * Channels that are present in the audio may be omitted, but it is an error
 * to include a channel index in this field that is not present in the audio.
 * Channels may be listed in any order but the same index may not be repeated
 * in this list.
 * 
 * BAD: `[0, 2]` for a stereo file; BAD: `[0, 0]` for a mono file.
 */
audioChannels: number[],
/**
 * This is an optional field.  If there is any metadata associated with the
 * audio being sent, use this field to provide it to cubic.  The server may
 * record this metadata when processing the request.  The server does not use
 * this field for any other purpose.
 */
metadata: RecognitionMetadata | undefined,
/**
 * This is an optional field for providing any additional context information
 * that may aid speech recognition.  This can also be used to add
 * out-of-vocabulary words to the model or boost recognition of specific
 * proper names or commands. Context information must be pre-compiled via the
 * `CompileContext()` method.
 */
context: RecognitionContext | undefined,
}

/**
 * The encoding of the audio data to be sent for recognition.
 * 
 * For best results, the audio source should be captured and transmitted using
 * the RAW_LINEAR16 encoding.
 */
export enum RecognitionConfig_Encoding {
/**
 * RAW_LINEAR16 - Raw (headerless) Uncompressed 16-bit signed little endian samples (linear
 * PCM), single channel, sampled at the rate expected by the chosen `Model`.
 */
RAW_LINEAR16 = 0,
/**
 * WAV - WAV (data with RIFF headers), with data sampled at a rate equal to or
 * higher than the sample rate expected by the chosen Model.
 */
WAV = 1,
/**
 * MP3 - MP3 data, sampled at a rate equal to or higher than the sampling rate
 * expected by the chosen Model.
 */
MP3 = 2,
/**
 * FLAC - FLAC data, sampled at a rate equal to or higher than the sample rate
 * expected by the chosen Model.
 */
FLAC = 3,
/** VOX8000 - VOX data (Dialogic ADPCM), sampled at 8 KHz. */
VOX8000 = 4,
/** ULAW8000 - μ-law (8-bit) encoded RAW data, single channel, sampled at 8 KHz. */
ULAW8000 = 5,
UNRECOGNIZED = -1,
}

export function recognitionConfig_EncodingFromJSON(object: any): RecognitionConfig_Encoding {
switch (object) {
case 0:
      case "RAW_LINEAR16":
        return RecognitionConfig_Encoding.RAW_LINEAR16;
case 1:
      case "WAV":
        return RecognitionConfig_Encoding.WAV;
case 2:
      case "MP3":
        return RecognitionConfig_Encoding.MP3;
case 3:
      case "FLAC":
        return RecognitionConfig_Encoding.FLAC;
case 4:
      case "VOX8000":
        return RecognitionConfig_Encoding.VOX8000;
case 5:
      case "ULAW8000":
        return RecognitionConfig_Encoding.ULAW8000;
case -1:
      case "UNRECOGNIZED":
      default:
        return RecognitionConfig_Encoding.UNRECOGNIZED;
}
}

export function recognitionConfig_EncodingToJSON(object: RecognitionConfig_Encoding): string {
switch (object) {
case RecognitionConfig_Encoding.RAW_LINEAR16: return "RAW_LINEAR16";
case RecognitionConfig_Encoding.WAV: return "WAV";
case RecognitionConfig_Encoding.MP3: return "MP3";
case RecognitionConfig_Encoding.FLAC: return "FLAC";
case RecognitionConfig_Encoding.VOX8000: return "VOX8000";
case RecognitionConfig_Encoding.ULAW8000: return "ULAW8000";
default: return "UNKNOWN";
}
}

/** Metadata associated with the audio to be recognized. */
export interface RecognitionMetadata {
/**
 * Any custom metadata that the client wants to associate with the recording.
 * This could be a simple string (e.g. a tracing ID) or structured data
 * (e.g. JSON)
 */
customMetadata: string,
}

/**
 * A collection of additional context information that may aid speech
 * recognition.  This can be used to add out-of-vocabulary words to  
 * the model or to boost recognition of specific proper names or commands.
 */
export interface RecognitionContext {
/**
 * List of compiled context information, with each entry being compiled from a
 * list of words or phrases using the `CompileContext` method.
 */
compiled: CompiledContext[],
}

/**
 * Context information in a compact form that is efficient for use in subsequent
 * recognition requests. The size of the compiled form will depend on the amount
 * of text that was sent for compilation. For 1000 words it's generally less
 * than 100 kilobytes.
 */
export interface CompiledContext {
/** The context information compiled by the `CompileContext` method. */
data: Uint8Array,
}

/**
 * A phrase or word that is to be compiled into context information that can be
 * later used to improve speech recognition during a `Recognize` or
 * `StreamingRecognize` call. Along with the phrase or word itself, there is an
 * optional boost parameter that can be used to boost the likelihood of the
 * phrase or word in the recognition output.
 */
export interface ContextPhrase {
/** The actual phrase or word. */
text: string,
/**
 * This is an optional field. The boost value is a positive number which is
 * used to increase the probability of the phrase or word appearing in the
 * output. This setting can be used to differentiate between similar sounding
 * words, with the desired word given a bigger boost value.
 * 
 * By default, all phrases or words are given an equal probability of 1/N
 * (where N = total number of phrases or words). If a boost value is provided,
 * the new probability is (boost + 1) * 1/N. We normalize the boosted
 * probabilities for all the phrases or words so that they sum to one. This
 * means that the boost value only has an effect if there are relative
 * differences in the values for different phrases or words. That is, if all
 * phrases or words have the same boost value, after normalization they will
 * all still have the same probability. This also means that the boost value
 * can be any positive value, but it is best to stick between 0 to 20.
 * 
 * Negative values are not supported and will be treated as 0 values.
 */
boost: number,
}

/** Audio to be sent to the recognizer */
export interface RecognitionAudio {
data: Uint8Array,
}

/** Description of a Cubic Model */
export interface Model {
/**
 * Unique identifier of the model.  This identifier is used to choose the
 * model that should be used for recognition, and is specified in the
 * `RecognitionConfig` message.
 */
id: string,
/**
 * Model name.  This is a concise name describing the model, and maybe
 * presented to the end-user, for example, to help choose which model to use
 * for their recognition task.
 */
name: string,
/** Model attributes */
attributes: ModelAttributes | undefined,
}

/** Attributes of a Cubic Model */
export interface ModelAttributes {
/** Audio sample rate supported by the model */
sampleRate: number,
/** Attributes specifc to supporting recognition context. */
contextInfo: ContextInfo | undefined,
}

/** Model information specifc to supporting recognition context. */
export interface ContextInfo {
/**
 * If this is set to true, the model supports taking context information into
 * account to aid speech recognition. The information may be sent with with
 * recognition requests via RecognitionContext inside RecognitionConfig.
 */
supportsContext: boolean,
/**
 * A list of tokens (e.g "name", "airport" etc.) that serve has placeholders
 * in the model where a client provided list of phrases or words may be used
 * to aid speech recognition and produce the exact desired recognition output.
 */
allowedContextTokens: string[],
}

/** A recognition result corresponding to a portion of audio. */
export interface RecognitionResult {
/** An n-best list of recognition hypotheses alternatives */
alternatives: RecognitionAlternative[],
/**
 * If this is set to true, it denotes that the result is an interim partial
 * result, and could change after more audio is processed.  If unset, or set
 * to false, it denotes that this is a final result and will not change.
 * 
 * Servers are not required to implement support for returning partial
 * results, and clients should generally not depend on their availability.
 */
isPartial: boolean,
/**
 * If `enable_confusion_network` was set to true in the `RecognitionConfig`,
 * and if the model supports it, a confusion network will be available in the
 * results.
 */
cnet: RecognitionConfusionNetwork | undefined,
/**
 * Channel of the audio file that this result was transcribed from.  For a
 * mono file, or RAW_LINEAR16 input, this will be set to 0.
 */
audioChannel: number,
}

/** A recognition hypothesis */
export interface RecognitionAlternative {
/**
 * Text representing the transcription of the words that the user spoke.
 * 
 * The transcript will be formatted according to the servers formatting
 * configuration. If you want the raw transcript, please see the field
 * `raw_transcript`.  If the server is configured to not use any formatting,
 * then this field will contain the raw transcript.
 * 
 * As an example, if the spoken utterance was "four people", and the
 * server was configured to format numbers, this field would be set to
 * "4 people".
 */
transcript: string,
/**
 * Text representing the transcription of the words that the user spoke,
 * without any formatting.  This field will be populated only the config
 * `RecognitionConfig.enable_raw_transcript` is set to true. Otherwise this
 * field will be an empty string. If you want the formatted transcript, please
 * see the field `transcript`.
 * 
 * As an example, if the spoken utterance was `here are four words`,
 * this field would be set to "HERE ARE FOUR WORDS".
 */
rawTranscript: string,
/**
 * Confidence estimate between 0 and 1. A higher number represents a higher
 * likelihood of the output being correct.
 */
confidence: number,
/**
 * A list of word-specific information for each recognized word in the
 * `transcript` field. This is available only if `enable_word_confidence` or
 * `enable_word_time_offsets` was set to `true` in the `RecognitionConfig`.
 */
words: WordInfo[],
/**
 * A list of word-specific information for each recognized word in the
 * `raw_transcript` field. This is available only if `enable_word_confidence`
 * or `enable_word_time_offsets` was set to `true` _and_
 * `enable_raw_transcript` is also set to `true` in the `RecognitionConfig`.
 */
rawWords: WordInfo[],
/**
 * Time offset relative to the beginning of audio received by the recognizer
 * and corresponding to the start of this utterance.
 */
startTime: Duration | undefined,
/** Duration of the current utterance in the spoken audio. */
duration: Duration | undefined,
}

/** Word-specific information for recognized words */
export interface WordInfo {
/** The actual word in the text */
word: string,
/**
 * Confidence estimate between 0 and 1.  A higher number represents a
 * higher likelihood that the word was correctly recognized.
 */
confidence: number,
/**
 * Time offset relative to the beginning of audio received by the recognizer
 * and corresponding to the start of this spoken word.
 */
startTime: Duration | undefined,
/** Duration of the current word in the spoken audio. */
duration: Duration | undefined,
}

/** Confusion network in recognition output */
export interface RecognitionConfusionNetwork {
links: ConfusionNetworkLink[],
}

/** A Link inside a confusion network */
export interface ConfusionNetworkLink {
/**
 * Time offset relative to the beginning of audio received by the recognizer
 * and corresponding to the start of this link
 */
startTime: Duration | undefined,
/** Duration of the current link in the confusion network */
duration: Duration | undefined,
/** Arcs between this link */
arcs: ConfusionNetworkArc[],
}

/** An Arc inside a Confusion Network Link */
export interface ConfusionNetworkArc {
/** Word in the recognized transcript */
word: string,
/**
 * Confidence estimate between 0 and 1.  A higher number represents a higher
 * likelihood that the word was correctly recognized.
 */
confidence: number,
}

const baseListModelsRequest: object = {  };

export const ListModelsRequest = {
            encode(
      _: ListModelsRequest,
      writer: Writer = Writer.create(),
    ): Writer {
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): ListModelsRequest {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseListModelsRequest } as ListModelsRequest;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(_: any): ListModelsRequest {
      const message = { ...baseListModelsRequest } as ListModelsRequest;
return message
},

toJSON(_: ListModelsRequest): unknown {
      const obj: any = {};
return obj;
},

fromPartial(_: DeepPartial<ListModelsRequest>): ListModelsRequest {
      const message = { ...baseListModelsRequest } as ListModelsRequest;
return message;
}
          };

const baseRecognizeRequest: object = {  };

export const RecognizeRequest = {
            encode(
      message: RecognizeRequest,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.config !== undefined) {
          RecognitionConfig.encode(message.config, writer.uint32(10).fork()).ldelim();
        }
if (message.audio !== undefined) {
          RecognitionAudio.encode(message.audio, writer.uint32(18).fork()).ldelim();
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): RecognizeRequest {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseRecognizeRequest } as RecognizeRequest;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.config = RecognitionConfig.decode(reader, reader.uint32());
break;
case 2:
message.audio = RecognitionAudio.decode(reader, reader.uint32());
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): RecognizeRequest {
      const message = { ...baseRecognizeRequest } as RecognizeRequest;
if (object.config !== undefined && object.config !== null) {
message.config = RecognitionConfig.fromJSON(object.config);
} else {
message.config = undefined;
}
if (object.audio !== undefined && object.audio !== null) {
message.audio = RecognitionAudio.fromJSON(object.audio);
} else {
message.audio = undefined;
}
return message
},

toJSON(message: RecognizeRequest): unknown {
      const obj: any = {};
message.config !== undefined && (obj.config = message.config ? RecognitionConfig.toJSON(message.config) : undefined);
message.audio !== undefined && (obj.audio = message.audio ? RecognitionAudio.toJSON(message.audio) : undefined);
return obj;
},

fromPartial(object: DeepPartial<RecognizeRequest>): RecognizeRequest {
      const message = { ...baseRecognizeRequest } as RecognizeRequest;
if (object.config !== undefined && object.config !== null) {
message.config = RecognitionConfig.fromPartial(object.config);
} else {
message.config = undefined
}
if (object.audio !== undefined && object.audio !== null) {
message.audio = RecognitionAudio.fromPartial(object.audio);
} else {
message.audio = undefined
}
return message;
}
          };

const baseStreamingRecognizeRequest: object = {  };

export const StreamingRecognizeRequest = {
            encode(
      message: StreamingRecognizeRequest,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.config !== undefined) {
          RecognitionConfig.encode(message.config, writer.uint32(10).fork()).ldelim();
        }
if (message.audio !== undefined) {
          RecognitionAudio.encode(message.audio, writer.uint32(18).fork()).ldelim();
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): StreamingRecognizeRequest {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseStreamingRecognizeRequest } as StreamingRecognizeRequest;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.config = RecognitionConfig.decode(reader, reader.uint32());
break;
case 2:
message.audio = RecognitionAudio.decode(reader, reader.uint32());
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): StreamingRecognizeRequest {
      const message = { ...baseStreamingRecognizeRequest } as StreamingRecognizeRequest;
if (object.config !== undefined && object.config !== null) {
message.config = RecognitionConfig.fromJSON(object.config);
} else {
message.config = undefined;
}
if (object.audio !== undefined && object.audio !== null) {
message.audio = RecognitionAudio.fromJSON(object.audio);
} else {
message.audio = undefined;
}
return message
},

toJSON(message: StreamingRecognizeRequest): unknown {
      const obj: any = {};
message.config !== undefined && (obj.config = message.config ? RecognitionConfig.toJSON(message.config) : undefined);
message.audio !== undefined && (obj.audio = message.audio ? RecognitionAudio.toJSON(message.audio) : undefined);
return obj;
},

fromPartial(object: DeepPartial<StreamingRecognizeRequest>): StreamingRecognizeRequest {
      const message = { ...baseStreamingRecognizeRequest } as StreamingRecognizeRequest;
if (object.config !== undefined && object.config !== null) {
message.config = RecognitionConfig.fromPartial(object.config);
} else {
message.config = undefined
}
if (object.audio !== undefined && object.audio !== null) {
message.audio = RecognitionAudio.fromPartial(object.audio);
} else {
message.audio = undefined
}
return message;
}
          };

const baseCompileContextRequest: object = { modelId: "",token: "" };

export const CompileContextRequest = {
            encode(
      message: CompileContextRequest,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.modelId !== "") {
          writer.uint32(10).string(message.modelId);
        }
if (message.token !== "") {
          writer.uint32(18).string(message.token);
        }
for (const v of message.phrases) {
            ContextPhrase.encode(v!, writer.uint32(26).fork()).ldelim();
          }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): CompileContextRequest {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseCompileContextRequest } as CompileContextRequest;
message.phrases = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.modelId = reader.string();
break;
case 2:
message.token = reader.string();
break;
case 3:
message.phrases.push(ContextPhrase.decode(reader, reader.uint32()));
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): CompileContextRequest {
      const message = { ...baseCompileContextRequest } as CompileContextRequest;
message.phrases = [];
if (object.modelId !== undefined && object.modelId !== null) {
message.modelId = String(object.modelId);
} else {
message.modelId = "";
}
if (object.token !== undefined && object.token !== null) {
message.token = String(object.token);
} else {
message.token = "";
}
if (object.phrases !== undefined && object.phrases !== null) {
for (const e of object.phrases) {
            message.phrases.push(ContextPhrase.fromJSON(e));
          }
}
return message
},

toJSON(message: CompileContextRequest): unknown {
      const obj: any = {};
message.modelId !== undefined && (obj.modelId = message.modelId);
message.token !== undefined && (obj.token = message.token);
if (message.phrases) {
          obj.phrases = message.phrases.map(e => e ? ContextPhrase.toJSON(e) : undefined);
        } else {
          obj.phrases = [];
        }
return obj;
},

fromPartial(object: DeepPartial<CompileContextRequest>): CompileContextRequest {
      const message = { ...baseCompileContextRequest } as CompileContextRequest;
message.phrases = [];
if (object.modelId !== undefined && object.modelId !== null) {
message.modelId = object.modelId;
} else {
message.modelId = ""
}
if (object.token !== undefined && object.token !== null) {
message.token = object.token;
} else {
message.token = ""
}
if (object.phrases !== undefined && object.phrases !== null) {
for (const e of object.phrases) {
            message.phrases.push(ContextPhrase.fromPartial(e));
          }
}
return message;
}
          };

const baseVersionResponse: object = { cubic: "",server: "" };

export const VersionResponse = {
            encode(
      message: VersionResponse,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.cubic !== "") {
          writer.uint32(10).string(message.cubic);
        }
if (message.server !== "") {
          writer.uint32(18).string(message.server);
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): VersionResponse {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseVersionResponse } as VersionResponse;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.cubic = reader.string();
break;
case 2:
message.server = reader.string();
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): VersionResponse {
      const message = { ...baseVersionResponse } as VersionResponse;
if (object.cubic !== undefined && object.cubic !== null) {
message.cubic = String(object.cubic);
} else {
message.cubic = "";
}
if (object.server !== undefined && object.server !== null) {
message.server = String(object.server);
} else {
message.server = "";
}
return message
},

toJSON(message: VersionResponse): unknown {
      const obj: any = {};
message.cubic !== undefined && (obj.cubic = message.cubic);
message.server !== undefined && (obj.server = message.server);
return obj;
},

fromPartial(object: DeepPartial<VersionResponse>): VersionResponse {
      const message = { ...baseVersionResponse } as VersionResponse;
if (object.cubic !== undefined && object.cubic !== null) {
message.cubic = object.cubic;
} else {
message.cubic = ""
}
if (object.server !== undefined && object.server !== null) {
message.server = object.server;
} else {
message.server = ""
}
return message;
}
          };

const baseListModelsResponse: object = {  };

export const ListModelsResponse = {
            encode(
      message: ListModelsResponse,
      writer: Writer = Writer.create(),
    ): Writer {
for (const v of message.models) {
            Model.encode(v!, writer.uint32(10).fork()).ldelim();
          }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): ListModelsResponse {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseListModelsResponse } as ListModelsResponse;
message.models = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.models.push(Model.decode(reader, reader.uint32()));
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): ListModelsResponse {
      const message = { ...baseListModelsResponse } as ListModelsResponse;
message.models = [];
if (object.models !== undefined && object.models !== null) {
for (const e of object.models) {
            message.models.push(Model.fromJSON(e));
          }
}
return message
},

toJSON(message: ListModelsResponse): unknown {
      const obj: any = {};
if (message.models) {
          obj.models = message.models.map(e => e ? Model.toJSON(e) : undefined);
        } else {
          obj.models = [];
        }
return obj;
},

fromPartial(object: DeepPartial<ListModelsResponse>): ListModelsResponse {
      const message = { ...baseListModelsResponse } as ListModelsResponse;
message.models = [];
if (object.models !== undefined && object.models !== null) {
for (const e of object.models) {
            message.models.push(Model.fromPartial(e));
          }
}
return message;
}
          };

const baseRecognitionResponse: object = {  };

export const RecognitionResponse = {
            encode(
      message: RecognitionResponse,
      writer: Writer = Writer.create(),
    ): Writer {
for (const v of message.results) {
            RecognitionResult.encode(v!, writer.uint32(10).fork()).ldelim();
          }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): RecognitionResponse {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseRecognitionResponse } as RecognitionResponse;
message.results = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.results.push(RecognitionResult.decode(reader, reader.uint32()));
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): RecognitionResponse {
      const message = { ...baseRecognitionResponse } as RecognitionResponse;
message.results = [];
if (object.results !== undefined && object.results !== null) {
for (const e of object.results) {
            message.results.push(RecognitionResult.fromJSON(e));
          }
}
return message
},

toJSON(message: RecognitionResponse): unknown {
      const obj: any = {};
if (message.results) {
          obj.results = message.results.map(e => e ? RecognitionResult.toJSON(e) : undefined);
        } else {
          obj.results = [];
        }
return obj;
},

fromPartial(object: DeepPartial<RecognitionResponse>): RecognitionResponse {
      const message = { ...baseRecognitionResponse } as RecognitionResponse;
message.results = [];
if (object.results !== undefined && object.results !== null) {
for (const e of object.results) {
            message.results.push(RecognitionResult.fromPartial(e));
          }
}
return message;
}
          };

const baseCompileContextResponse: object = {  };

export const CompileContextResponse = {
            encode(
      message: CompileContextResponse,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.context !== undefined) {
          CompiledContext.encode(message.context, writer.uint32(10).fork()).ldelim();
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): CompileContextResponse {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseCompileContextResponse } as CompileContextResponse;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.context = CompiledContext.decode(reader, reader.uint32());
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): CompileContextResponse {
      const message = { ...baseCompileContextResponse } as CompileContextResponse;
if (object.context !== undefined && object.context !== null) {
message.context = CompiledContext.fromJSON(object.context);
} else {
message.context = undefined;
}
return message
},

toJSON(message: CompileContextResponse): unknown {
      const obj: any = {};
message.context !== undefined && (obj.context = message.context ? CompiledContext.toJSON(message.context) : undefined);
return obj;
},

fromPartial(object: DeepPartial<CompileContextResponse>): CompileContextResponse {
      const message = { ...baseCompileContextResponse } as CompileContextResponse;
if (object.context !== undefined && object.context !== null) {
message.context = CompiledContext.fromPartial(object.context);
} else {
message.context = undefined
}
return message;
}
          };

const baseRecognitionConfig: object = { modelId: "",audioEncoding: 0,enableWordTimeOffsets: false,enableWordConfidence: false,enableRawTranscript: false,enableConfusionNetwork: false,audioChannels: 0 };

export const RecognitionConfig = {
            encode(
      message: RecognitionConfig,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.modelId !== "") {
          writer.uint32(10).string(message.modelId);
        }
if (message.audioEncoding !== 0) {
          writer.uint32(16).int32(message.audioEncoding);
        }
if (message.idleTimeout !== undefined) {
          Duration.encode(message.idleTimeout, writer.uint32(26).fork()).ldelim();
        }
if (message.enableWordTimeOffsets === true) {
          writer.uint32(32).bool(message.enableWordTimeOffsets);
        }
if (message.enableWordConfidence === true) {
          writer.uint32(40).bool(message.enableWordConfidence);
        }
if (message.enableRawTranscript === true) {
          writer.uint32(48).bool(message.enableRawTranscript);
        }
if (message.enableConfusionNetwork === true) {
          writer.uint32(56).bool(message.enableConfusionNetwork);
        }
writer.uint32(66).fork();
          for (const v of message.audioChannels) {
            writer.uint32(v);
          }
          writer.ldelim();
if (message.metadata !== undefined) {
          RecognitionMetadata.encode(message.metadata, writer.uint32(74).fork()).ldelim();
        }
if (message.context !== undefined) {
          RecognitionContext.encode(message.context, writer.uint32(82).fork()).ldelim();
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): RecognitionConfig {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseRecognitionConfig } as RecognitionConfig;
message.audioChannels = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.modelId = reader.string();
break;
case 2:
message.audioEncoding = reader.int32() as any;
break;
case 3:
message.idleTimeout = Duration.decode(reader, reader.uint32());
break;
case 4:
message.enableWordTimeOffsets = reader.bool();
break;
case 5:
message.enableWordConfidence = reader.bool();
break;
case 6:
message.enableRawTranscript = reader.bool();
break;
case 7:
message.enableConfusionNetwork = reader.bool();
break;
case 8:
if ((tag & 7) === 2) {
            const end2 = reader.uint32() + reader.pos;
            while (reader.pos < end2) {
              message.audioChannels.push(reader.uint32());
            }
          } else {
            message.audioChannels.push(reader.uint32());
          }
break;
case 9:
message.metadata = RecognitionMetadata.decode(reader, reader.uint32());
break;
case 10:
message.context = RecognitionContext.decode(reader, reader.uint32());
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): RecognitionConfig {
      const message = { ...baseRecognitionConfig } as RecognitionConfig;
message.audioChannels = [];
if (object.modelId !== undefined && object.modelId !== null) {
message.modelId = String(object.modelId);
} else {
message.modelId = "";
}
if (object.audioEncoding !== undefined && object.audioEncoding !== null) {
message.audioEncoding = recognitionConfig_EncodingFromJSON(object.audioEncoding);
} else {
message.audioEncoding = 0;
}
if (object.idleTimeout !== undefined && object.idleTimeout !== null) {
message.idleTimeout = Duration.fromJSON(object.idleTimeout);
} else {
message.idleTimeout = undefined;
}
if (object.enableWordTimeOffsets !== undefined && object.enableWordTimeOffsets !== null) {
message.enableWordTimeOffsets = Boolean(object.enableWordTimeOffsets);
} else {
message.enableWordTimeOffsets = false;
}
if (object.enableWordConfidence !== undefined && object.enableWordConfidence !== null) {
message.enableWordConfidence = Boolean(object.enableWordConfidence);
} else {
message.enableWordConfidence = false;
}
if (object.enableRawTranscript !== undefined && object.enableRawTranscript !== null) {
message.enableRawTranscript = Boolean(object.enableRawTranscript);
} else {
message.enableRawTranscript = false;
}
if (object.enableConfusionNetwork !== undefined && object.enableConfusionNetwork !== null) {
message.enableConfusionNetwork = Boolean(object.enableConfusionNetwork);
} else {
message.enableConfusionNetwork = false;
}
if (object.audioChannels !== undefined && object.audioChannels !== null) {
for (const e of object.audioChannels) {
            message.audioChannels.push(Number(e));
          }
}
if (object.metadata !== undefined && object.metadata !== null) {
message.metadata = RecognitionMetadata.fromJSON(object.metadata);
} else {
message.metadata = undefined;
}
if (object.context !== undefined && object.context !== null) {
message.context = RecognitionContext.fromJSON(object.context);
} else {
message.context = undefined;
}
return message
},

toJSON(message: RecognitionConfig): unknown {
      const obj: any = {};
message.modelId !== undefined && (obj.modelId = message.modelId);
message.audioEncoding !== undefined && (obj.audioEncoding = recognitionConfig_EncodingToJSON(message.audioEncoding));
message.idleTimeout !== undefined && (obj.idleTimeout = message.idleTimeout ? Duration.toJSON(message.idleTimeout) : undefined);
message.enableWordTimeOffsets !== undefined && (obj.enableWordTimeOffsets = message.enableWordTimeOffsets);
message.enableWordConfidence !== undefined && (obj.enableWordConfidence = message.enableWordConfidence);
message.enableRawTranscript !== undefined && (obj.enableRawTranscript = message.enableRawTranscript);
message.enableConfusionNetwork !== undefined && (obj.enableConfusionNetwork = message.enableConfusionNetwork);
if (message.audioChannels) {
          obj.audioChannels = message.audioChannels.map(e => e);
        } else {
          obj.audioChannels = [];
        }
message.metadata !== undefined && (obj.metadata = message.metadata ? RecognitionMetadata.toJSON(message.metadata) : undefined);
message.context !== undefined && (obj.context = message.context ? RecognitionContext.toJSON(message.context) : undefined);
return obj;
},

fromPartial(object: DeepPartial<RecognitionConfig>): RecognitionConfig {
      const message = { ...baseRecognitionConfig } as RecognitionConfig;
message.audioChannels = [];
if (object.modelId !== undefined && object.modelId !== null) {
message.modelId = object.modelId;
} else {
message.modelId = ""
}
if (object.audioEncoding !== undefined && object.audioEncoding !== null) {
message.audioEncoding = object.audioEncoding;
} else {
message.audioEncoding = 0
}
if (object.idleTimeout !== undefined && object.idleTimeout !== null) {
message.idleTimeout = Duration.fromPartial(object.idleTimeout);
} else {
message.idleTimeout = undefined
}
if (object.enableWordTimeOffsets !== undefined && object.enableWordTimeOffsets !== null) {
message.enableWordTimeOffsets = object.enableWordTimeOffsets;
} else {
message.enableWordTimeOffsets = false
}
if (object.enableWordConfidence !== undefined && object.enableWordConfidence !== null) {
message.enableWordConfidence = object.enableWordConfidence;
} else {
message.enableWordConfidence = false
}
if (object.enableRawTranscript !== undefined && object.enableRawTranscript !== null) {
message.enableRawTranscript = object.enableRawTranscript;
} else {
message.enableRawTranscript = false
}
if (object.enableConfusionNetwork !== undefined && object.enableConfusionNetwork !== null) {
message.enableConfusionNetwork = object.enableConfusionNetwork;
} else {
message.enableConfusionNetwork = false
}
if (object.audioChannels !== undefined && object.audioChannels !== null) {
for (const e of object.audioChannels) {
            message.audioChannels.push(e);
          }
}
if (object.metadata !== undefined && object.metadata !== null) {
message.metadata = RecognitionMetadata.fromPartial(object.metadata);
} else {
message.metadata = undefined
}
if (object.context !== undefined && object.context !== null) {
message.context = RecognitionContext.fromPartial(object.context);
} else {
message.context = undefined
}
return message;
}
          };

const baseRecognitionMetadata: object = { customMetadata: "" };

export const RecognitionMetadata = {
            encode(
      message: RecognitionMetadata,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.customMetadata !== "") {
          writer.uint32(10).string(message.customMetadata);
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): RecognitionMetadata {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseRecognitionMetadata } as RecognitionMetadata;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.customMetadata = reader.string();
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): RecognitionMetadata {
      const message = { ...baseRecognitionMetadata } as RecognitionMetadata;
if (object.customMetadata !== undefined && object.customMetadata !== null) {
message.customMetadata = String(object.customMetadata);
} else {
message.customMetadata = "";
}
return message
},

toJSON(message: RecognitionMetadata): unknown {
      const obj: any = {};
message.customMetadata !== undefined && (obj.customMetadata = message.customMetadata);
return obj;
},

fromPartial(object: DeepPartial<RecognitionMetadata>): RecognitionMetadata {
      const message = { ...baseRecognitionMetadata } as RecognitionMetadata;
if (object.customMetadata !== undefined && object.customMetadata !== null) {
message.customMetadata = object.customMetadata;
} else {
message.customMetadata = ""
}
return message;
}
          };

const baseRecognitionContext: object = {  };

export const RecognitionContext = {
            encode(
      message: RecognitionContext,
      writer: Writer = Writer.create(),
    ): Writer {
for (const v of message.compiled) {
            CompiledContext.encode(v!, writer.uint32(10).fork()).ldelim();
          }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): RecognitionContext {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseRecognitionContext } as RecognitionContext;
message.compiled = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.compiled.push(CompiledContext.decode(reader, reader.uint32()));
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): RecognitionContext {
      const message = { ...baseRecognitionContext } as RecognitionContext;
message.compiled = [];
if (object.compiled !== undefined && object.compiled !== null) {
for (const e of object.compiled) {
            message.compiled.push(CompiledContext.fromJSON(e));
          }
}
return message
},

toJSON(message: RecognitionContext): unknown {
      const obj: any = {};
if (message.compiled) {
          obj.compiled = message.compiled.map(e => e ? CompiledContext.toJSON(e) : undefined);
        } else {
          obj.compiled = [];
        }
return obj;
},

fromPartial(object: DeepPartial<RecognitionContext>): RecognitionContext {
      const message = { ...baseRecognitionContext } as RecognitionContext;
message.compiled = [];
if (object.compiled !== undefined && object.compiled !== null) {
for (const e of object.compiled) {
            message.compiled.push(CompiledContext.fromPartial(e));
          }
}
return message;
}
          };

const baseCompiledContext: object = {  };

export const CompiledContext = {
            encode(
      message: CompiledContext,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.data.length !== 0) {
          writer.uint32(10).bytes(message.data);
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): CompiledContext {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseCompiledContext } as CompiledContext;
message.data = new Uint8Array();
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.data = reader.bytes();
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): CompiledContext {
      const message = { ...baseCompiledContext } as CompiledContext;
message.data = new Uint8Array();
if (object.data !== undefined && object.data !== null) {
message.data = bytesFromBase64(object.data);
}
return message
},

toJSON(message: CompiledContext): unknown {
      const obj: any = {};
message.data !== undefined && (obj.data = base64FromBytes(message.data !== undefined ? message.data : new Uint8Array()));
return obj;
},

fromPartial(object: DeepPartial<CompiledContext>): CompiledContext {
      const message = { ...baseCompiledContext } as CompiledContext;
if (object.data !== undefined && object.data !== null) {
message.data = object.data;
} else {
message.data = new Uint8Array()
}
return message;
}
          };

const baseContextPhrase: object = { text: "",boost: 0 };

export const ContextPhrase = {
            encode(
      message: ContextPhrase,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.text !== "") {
          writer.uint32(10).string(message.text);
        }
if (message.boost !== 0) {
          writer.uint32(21).float(message.boost);
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): ContextPhrase {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseContextPhrase } as ContextPhrase;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.text = reader.string();
break;
case 2:
message.boost = reader.float();
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): ContextPhrase {
      const message = { ...baseContextPhrase } as ContextPhrase;
if (object.text !== undefined && object.text !== null) {
message.text = String(object.text);
} else {
message.text = "";
}
if (object.boost !== undefined && object.boost !== null) {
message.boost = Number(object.boost);
} else {
message.boost = 0;
}
return message
},

toJSON(message: ContextPhrase): unknown {
      const obj: any = {};
message.text !== undefined && (obj.text = message.text);
message.boost !== undefined && (obj.boost = message.boost);
return obj;
},

fromPartial(object: DeepPartial<ContextPhrase>): ContextPhrase {
      const message = { ...baseContextPhrase } as ContextPhrase;
if (object.text !== undefined && object.text !== null) {
message.text = object.text;
} else {
message.text = ""
}
if (object.boost !== undefined && object.boost !== null) {
message.boost = object.boost;
} else {
message.boost = 0
}
return message;
}
          };

const baseRecognitionAudio: object = {  };

export const RecognitionAudio = {
            encode(
      message: RecognitionAudio,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.data.length !== 0) {
          writer.uint32(10).bytes(message.data);
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): RecognitionAudio {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseRecognitionAudio } as RecognitionAudio;
message.data = new Uint8Array();
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.data = reader.bytes();
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): RecognitionAudio {
      const message = { ...baseRecognitionAudio } as RecognitionAudio;
message.data = new Uint8Array();
if (object.data !== undefined && object.data !== null) {
message.data = bytesFromBase64(object.data);
}
return message
},

toJSON(message: RecognitionAudio): unknown {
      const obj: any = {};
message.data !== undefined && (obj.data = base64FromBytes(message.data !== undefined ? message.data : new Uint8Array()));
return obj;
},

fromPartial(object: DeepPartial<RecognitionAudio>): RecognitionAudio {
      const message = { ...baseRecognitionAudio } as RecognitionAudio;
if (object.data !== undefined && object.data !== null) {
message.data = object.data;
} else {
message.data = new Uint8Array()
}
return message;
}
          };

const baseModel: object = { id: "",name: "" };

export const Model = {
            encode(
      message: Model,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.id !== "") {
          writer.uint32(10).string(message.id);
        }
if (message.name !== "") {
          writer.uint32(18).string(message.name);
        }
if (message.attributes !== undefined) {
          ModelAttributes.encode(message.attributes, writer.uint32(26).fork()).ldelim();
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): Model {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseModel } as Model;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.id = reader.string();
break;
case 2:
message.name = reader.string();
break;
case 3:
message.attributes = ModelAttributes.decode(reader, reader.uint32());
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): Model {
      const message = { ...baseModel } as Model;
if (object.id !== undefined && object.id !== null) {
message.id = String(object.id);
} else {
message.id = "";
}
if (object.name !== undefined && object.name !== null) {
message.name = String(object.name);
} else {
message.name = "";
}
if (object.attributes !== undefined && object.attributes !== null) {
message.attributes = ModelAttributes.fromJSON(object.attributes);
} else {
message.attributes = undefined;
}
return message
},

toJSON(message: Model): unknown {
      const obj: any = {};
message.id !== undefined && (obj.id = message.id);
message.name !== undefined && (obj.name = message.name);
message.attributes !== undefined && (obj.attributes = message.attributes ? ModelAttributes.toJSON(message.attributes) : undefined);
return obj;
},

fromPartial(object: DeepPartial<Model>): Model {
      const message = { ...baseModel } as Model;
if (object.id !== undefined && object.id !== null) {
message.id = object.id;
} else {
message.id = ""
}
if (object.name !== undefined && object.name !== null) {
message.name = object.name;
} else {
message.name = ""
}
if (object.attributes !== undefined && object.attributes !== null) {
message.attributes = ModelAttributes.fromPartial(object.attributes);
} else {
message.attributes = undefined
}
return message;
}
          };

const baseModelAttributes: object = { sampleRate: 0 };

export const ModelAttributes = {
            encode(
      message: ModelAttributes,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.sampleRate !== 0) {
          writer.uint32(8).uint32(message.sampleRate);
        }
if (message.contextInfo !== undefined) {
          ContextInfo.encode(message.contextInfo, writer.uint32(18).fork()).ldelim();
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): ModelAttributes {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseModelAttributes } as ModelAttributes;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.sampleRate = reader.uint32();
break;
case 2:
message.contextInfo = ContextInfo.decode(reader, reader.uint32());
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): ModelAttributes {
      const message = { ...baseModelAttributes } as ModelAttributes;
if (object.sampleRate !== undefined && object.sampleRate !== null) {
message.sampleRate = Number(object.sampleRate);
} else {
message.sampleRate = 0;
}
if (object.contextInfo !== undefined && object.contextInfo !== null) {
message.contextInfo = ContextInfo.fromJSON(object.contextInfo);
} else {
message.contextInfo = undefined;
}
return message
},

toJSON(message: ModelAttributes): unknown {
      const obj: any = {};
message.sampleRate !== undefined && (obj.sampleRate = message.sampleRate);
message.contextInfo !== undefined && (obj.contextInfo = message.contextInfo ? ContextInfo.toJSON(message.contextInfo) : undefined);
return obj;
},

fromPartial(object: DeepPartial<ModelAttributes>): ModelAttributes {
      const message = { ...baseModelAttributes } as ModelAttributes;
if (object.sampleRate !== undefined && object.sampleRate !== null) {
message.sampleRate = object.sampleRate;
} else {
message.sampleRate = 0
}
if (object.contextInfo !== undefined && object.contextInfo !== null) {
message.contextInfo = ContextInfo.fromPartial(object.contextInfo);
} else {
message.contextInfo = undefined
}
return message;
}
          };

const baseContextInfo: object = { supportsContext: false,allowedContextTokens: "" };

export const ContextInfo = {
            encode(
      message: ContextInfo,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.supportsContext === true) {
          writer.uint32(8).bool(message.supportsContext);
        }
for (const v of message.allowedContextTokens) {
            writer.uint32(18).string(v!);
          }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): ContextInfo {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseContextInfo } as ContextInfo;
message.allowedContextTokens = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.supportsContext = reader.bool();
break;
case 2:
message.allowedContextTokens.push(reader.string());
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): ContextInfo {
      const message = { ...baseContextInfo } as ContextInfo;
message.allowedContextTokens = [];
if (object.supportsContext !== undefined && object.supportsContext !== null) {
message.supportsContext = Boolean(object.supportsContext);
} else {
message.supportsContext = false;
}
if (object.allowedContextTokens !== undefined && object.allowedContextTokens !== null) {
for (const e of object.allowedContextTokens) {
            message.allowedContextTokens.push(String(e));
          }
}
return message
},

toJSON(message: ContextInfo): unknown {
      const obj: any = {};
message.supportsContext !== undefined && (obj.supportsContext = message.supportsContext);
if (message.allowedContextTokens) {
          obj.allowedContextTokens = message.allowedContextTokens.map(e => e);
        } else {
          obj.allowedContextTokens = [];
        }
return obj;
},

fromPartial(object: DeepPartial<ContextInfo>): ContextInfo {
      const message = { ...baseContextInfo } as ContextInfo;
message.allowedContextTokens = [];
if (object.supportsContext !== undefined && object.supportsContext !== null) {
message.supportsContext = object.supportsContext;
} else {
message.supportsContext = false
}
if (object.allowedContextTokens !== undefined && object.allowedContextTokens !== null) {
for (const e of object.allowedContextTokens) {
            message.allowedContextTokens.push(e);
          }
}
return message;
}
          };

const baseRecognitionResult: object = { isPartial: false,audioChannel: 0 };

export const RecognitionResult = {
            encode(
      message: RecognitionResult,
      writer: Writer = Writer.create(),
    ): Writer {
for (const v of message.alternatives) {
            RecognitionAlternative.encode(v!, writer.uint32(10).fork()).ldelim();
          }
if (message.isPartial === true) {
          writer.uint32(16).bool(message.isPartial);
        }
if (message.cnet !== undefined) {
          RecognitionConfusionNetwork.encode(message.cnet, writer.uint32(26).fork()).ldelim();
        }
if (message.audioChannel !== 0) {
          writer.uint32(32).uint32(message.audioChannel);
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): RecognitionResult {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseRecognitionResult } as RecognitionResult;
message.alternatives = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.alternatives.push(RecognitionAlternative.decode(reader, reader.uint32()));
break;
case 2:
message.isPartial = reader.bool();
break;
case 3:
message.cnet = RecognitionConfusionNetwork.decode(reader, reader.uint32());
break;
case 4:
message.audioChannel = reader.uint32();
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): RecognitionResult {
      const message = { ...baseRecognitionResult } as RecognitionResult;
message.alternatives = [];
if (object.alternatives !== undefined && object.alternatives !== null) {
for (const e of object.alternatives) {
            message.alternatives.push(RecognitionAlternative.fromJSON(e));
          }
}
if (object.isPartial !== undefined && object.isPartial !== null) {
message.isPartial = Boolean(object.isPartial);
} else {
message.isPartial = false;
}
if (object.cnet !== undefined && object.cnet !== null) {
message.cnet = RecognitionConfusionNetwork.fromJSON(object.cnet);
} else {
message.cnet = undefined;
}
if (object.audioChannel !== undefined && object.audioChannel !== null) {
message.audioChannel = Number(object.audioChannel);
} else {
message.audioChannel = 0;
}
return message
},

toJSON(message: RecognitionResult): unknown {
      const obj: any = {};
if (message.alternatives) {
          obj.alternatives = message.alternatives.map(e => e ? RecognitionAlternative.toJSON(e) : undefined);
        } else {
          obj.alternatives = [];
        }
message.isPartial !== undefined && (obj.isPartial = message.isPartial);
message.cnet !== undefined && (obj.cnet = message.cnet ? RecognitionConfusionNetwork.toJSON(message.cnet) : undefined);
message.audioChannel !== undefined && (obj.audioChannel = message.audioChannel);
return obj;
},

fromPartial(object: DeepPartial<RecognitionResult>): RecognitionResult {
      const message = { ...baseRecognitionResult } as RecognitionResult;
message.alternatives = [];
if (object.alternatives !== undefined && object.alternatives !== null) {
for (const e of object.alternatives) {
            message.alternatives.push(RecognitionAlternative.fromPartial(e));
          }
}
if (object.isPartial !== undefined && object.isPartial !== null) {
message.isPartial = object.isPartial;
} else {
message.isPartial = false
}
if (object.cnet !== undefined && object.cnet !== null) {
message.cnet = RecognitionConfusionNetwork.fromPartial(object.cnet);
} else {
message.cnet = undefined
}
if (object.audioChannel !== undefined && object.audioChannel !== null) {
message.audioChannel = object.audioChannel;
} else {
message.audioChannel = 0
}
return message;
}
          };

const baseRecognitionAlternative: object = { transcript: "",rawTranscript: "",confidence: 0 };

export const RecognitionAlternative = {
            encode(
      message: RecognitionAlternative,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.transcript !== "") {
          writer.uint32(10).string(message.transcript);
        }
if (message.rawTranscript !== "") {
          writer.uint32(50).string(message.rawTranscript);
        }
if (message.confidence !== 0) {
          writer.uint32(17).double(message.confidence);
        }
for (const v of message.words) {
            WordInfo.encode(v!, writer.uint32(26).fork()).ldelim();
          }
for (const v of message.rawWords) {
            WordInfo.encode(v!, writer.uint32(58).fork()).ldelim();
          }
if (message.startTime !== undefined) {
          Duration.encode(message.startTime, writer.uint32(34).fork()).ldelim();
        }
if (message.duration !== undefined) {
          Duration.encode(message.duration, writer.uint32(42).fork()).ldelim();
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): RecognitionAlternative {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseRecognitionAlternative } as RecognitionAlternative;
message.words = [];
message.rawWords = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.transcript = reader.string();
break;
case 6:
message.rawTranscript = reader.string();
break;
case 2:
message.confidence = reader.double();
break;
case 3:
message.words.push(WordInfo.decode(reader, reader.uint32()));
break;
case 7:
message.rawWords.push(WordInfo.decode(reader, reader.uint32()));
break;
case 4:
message.startTime = Duration.decode(reader, reader.uint32());
break;
case 5:
message.duration = Duration.decode(reader, reader.uint32());
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): RecognitionAlternative {
      const message = { ...baseRecognitionAlternative } as RecognitionAlternative;
message.words = [];
message.rawWords = [];
if (object.transcript !== undefined && object.transcript !== null) {
message.transcript = String(object.transcript);
} else {
message.transcript = "";
}
if (object.rawTranscript !== undefined && object.rawTranscript !== null) {
message.rawTranscript = String(object.rawTranscript);
} else {
message.rawTranscript = "";
}
if (object.confidence !== undefined && object.confidence !== null) {
message.confidence = Number(object.confidence);
} else {
message.confidence = 0;
}
if (object.words !== undefined && object.words !== null) {
for (const e of object.words) {
            message.words.push(WordInfo.fromJSON(e));
          }
}
if (object.rawWords !== undefined && object.rawWords !== null) {
for (const e of object.rawWords) {
            message.rawWords.push(WordInfo.fromJSON(e));
          }
}
if (object.startTime !== undefined && object.startTime !== null) {
message.startTime = Duration.fromJSON(object.startTime);
} else {
message.startTime = undefined;
}
if (object.duration !== undefined && object.duration !== null) {
message.duration = Duration.fromJSON(object.duration);
} else {
message.duration = undefined;
}
return message
},

toJSON(message: RecognitionAlternative): unknown {
      const obj: any = {};
message.transcript !== undefined && (obj.transcript = message.transcript);
message.rawTranscript !== undefined && (obj.rawTranscript = message.rawTranscript);
message.confidence !== undefined && (obj.confidence = message.confidence);
if (message.words) {
          obj.words = message.words.map(e => e ? WordInfo.toJSON(e) : undefined);
        } else {
          obj.words = [];
        }
if (message.rawWords) {
          obj.rawWords = message.rawWords.map(e => e ? WordInfo.toJSON(e) : undefined);
        } else {
          obj.rawWords = [];
        }
message.startTime !== undefined && (obj.startTime = message.startTime ? Duration.toJSON(message.startTime) : undefined);
message.duration !== undefined && (obj.duration = message.duration ? Duration.toJSON(message.duration) : undefined);
return obj;
},

fromPartial(object: DeepPartial<RecognitionAlternative>): RecognitionAlternative {
      const message = { ...baseRecognitionAlternative } as RecognitionAlternative;
message.words = [];
message.rawWords = [];
if (object.transcript !== undefined && object.transcript !== null) {
message.transcript = object.transcript;
} else {
message.transcript = ""
}
if (object.rawTranscript !== undefined && object.rawTranscript !== null) {
message.rawTranscript = object.rawTranscript;
} else {
message.rawTranscript = ""
}
if (object.confidence !== undefined && object.confidence !== null) {
message.confidence = object.confidence;
} else {
message.confidence = 0
}
if (object.words !== undefined && object.words !== null) {
for (const e of object.words) {
            message.words.push(WordInfo.fromPartial(e));
          }
}
if (object.rawWords !== undefined && object.rawWords !== null) {
for (const e of object.rawWords) {
            message.rawWords.push(WordInfo.fromPartial(e));
          }
}
if (object.startTime !== undefined && object.startTime !== null) {
message.startTime = Duration.fromPartial(object.startTime);
} else {
message.startTime = undefined
}
if (object.duration !== undefined && object.duration !== null) {
message.duration = Duration.fromPartial(object.duration);
} else {
message.duration = undefined
}
return message;
}
          };

const baseWordInfo: object = { word: "",confidence: 0 };

export const WordInfo = {
            encode(
      message: WordInfo,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.word !== "") {
          writer.uint32(10).string(message.word);
        }
if (message.confidence !== 0) {
          writer.uint32(17).double(message.confidence);
        }
if (message.startTime !== undefined) {
          Duration.encode(message.startTime, writer.uint32(26).fork()).ldelim();
        }
if (message.duration !== undefined) {
          Duration.encode(message.duration, writer.uint32(34).fork()).ldelim();
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): WordInfo {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseWordInfo } as WordInfo;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.word = reader.string();
break;
case 2:
message.confidence = reader.double();
break;
case 3:
message.startTime = Duration.decode(reader, reader.uint32());
break;
case 4:
message.duration = Duration.decode(reader, reader.uint32());
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): WordInfo {
      const message = { ...baseWordInfo } as WordInfo;
if (object.word !== undefined && object.word !== null) {
message.word = String(object.word);
} else {
message.word = "";
}
if (object.confidence !== undefined && object.confidence !== null) {
message.confidence = Number(object.confidence);
} else {
message.confidence = 0;
}
if (object.startTime !== undefined && object.startTime !== null) {
message.startTime = Duration.fromJSON(object.startTime);
} else {
message.startTime = undefined;
}
if (object.duration !== undefined && object.duration !== null) {
message.duration = Duration.fromJSON(object.duration);
} else {
message.duration = undefined;
}
return message
},

toJSON(message: WordInfo): unknown {
      const obj: any = {};
message.word !== undefined && (obj.word = message.word);
message.confidence !== undefined && (obj.confidence = message.confidence);
message.startTime !== undefined && (obj.startTime = message.startTime ? Duration.toJSON(message.startTime) : undefined);
message.duration !== undefined && (obj.duration = message.duration ? Duration.toJSON(message.duration) : undefined);
return obj;
},

fromPartial(object: DeepPartial<WordInfo>): WordInfo {
      const message = { ...baseWordInfo } as WordInfo;
if (object.word !== undefined && object.word !== null) {
message.word = object.word;
} else {
message.word = ""
}
if (object.confidence !== undefined && object.confidence !== null) {
message.confidence = object.confidence;
} else {
message.confidence = 0
}
if (object.startTime !== undefined && object.startTime !== null) {
message.startTime = Duration.fromPartial(object.startTime);
} else {
message.startTime = undefined
}
if (object.duration !== undefined && object.duration !== null) {
message.duration = Duration.fromPartial(object.duration);
} else {
message.duration = undefined
}
return message;
}
          };

const baseRecognitionConfusionNetwork: object = {  };

export const RecognitionConfusionNetwork = {
            encode(
      message: RecognitionConfusionNetwork,
      writer: Writer = Writer.create(),
    ): Writer {
for (const v of message.links) {
            ConfusionNetworkLink.encode(v!, writer.uint32(10).fork()).ldelim();
          }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): RecognitionConfusionNetwork {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseRecognitionConfusionNetwork } as RecognitionConfusionNetwork;
message.links = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.links.push(ConfusionNetworkLink.decode(reader, reader.uint32()));
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): RecognitionConfusionNetwork {
      const message = { ...baseRecognitionConfusionNetwork } as RecognitionConfusionNetwork;
message.links = [];
if (object.links !== undefined && object.links !== null) {
for (const e of object.links) {
            message.links.push(ConfusionNetworkLink.fromJSON(e));
          }
}
return message
},

toJSON(message: RecognitionConfusionNetwork): unknown {
      const obj: any = {};
if (message.links) {
          obj.links = message.links.map(e => e ? ConfusionNetworkLink.toJSON(e) : undefined);
        } else {
          obj.links = [];
        }
return obj;
},

fromPartial(object: DeepPartial<RecognitionConfusionNetwork>): RecognitionConfusionNetwork {
      const message = { ...baseRecognitionConfusionNetwork } as RecognitionConfusionNetwork;
message.links = [];
if (object.links !== undefined && object.links !== null) {
for (const e of object.links) {
            message.links.push(ConfusionNetworkLink.fromPartial(e));
          }
}
return message;
}
          };

const baseConfusionNetworkLink: object = {  };

export const ConfusionNetworkLink = {
            encode(
      message: ConfusionNetworkLink,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.startTime !== undefined) {
          Duration.encode(message.startTime, writer.uint32(10).fork()).ldelim();
        }
if (message.duration !== undefined) {
          Duration.encode(message.duration, writer.uint32(18).fork()).ldelim();
        }
for (const v of message.arcs) {
            ConfusionNetworkArc.encode(v!, writer.uint32(26).fork()).ldelim();
          }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): ConfusionNetworkLink {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseConfusionNetworkLink } as ConfusionNetworkLink;
message.arcs = [];
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.startTime = Duration.decode(reader, reader.uint32());
break;
case 2:
message.duration = Duration.decode(reader, reader.uint32());
break;
case 3:
message.arcs.push(ConfusionNetworkArc.decode(reader, reader.uint32()));
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): ConfusionNetworkLink {
      const message = { ...baseConfusionNetworkLink } as ConfusionNetworkLink;
message.arcs = [];
if (object.startTime !== undefined && object.startTime !== null) {
message.startTime = Duration.fromJSON(object.startTime);
} else {
message.startTime = undefined;
}
if (object.duration !== undefined && object.duration !== null) {
message.duration = Duration.fromJSON(object.duration);
} else {
message.duration = undefined;
}
if (object.arcs !== undefined && object.arcs !== null) {
for (const e of object.arcs) {
            message.arcs.push(ConfusionNetworkArc.fromJSON(e));
          }
}
return message
},

toJSON(message: ConfusionNetworkLink): unknown {
      const obj: any = {};
message.startTime !== undefined && (obj.startTime = message.startTime ? Duration.toJSON(message.startTime) : undefined);
message.duration !== undefined && (obj.duration = message.duration ? Duration.toJSON(message.duration) : undefined);
if (message.arcs) {
          obj.arcs = message.arcs.map(e => e ? ConfusionNetworkArc.toJSON(e) : undefined);
        } else {
          obj.arcs = [];
        }
return obj;
},

fromPartial(object: DeepPartial<ConfusionNetworkLink>): ConfusionNetworkLink {
      const message = { ...baseConfusionNetworkLink } as ConfusionNetworkLink;
message.arcs = [];
if (object.startTime !== undefined && object.startTime !== null) {
message.startTime = Duration.fromPartial(object.startTime);
} else {
message.startTime = undefined
}
if (object.duration !== undefined && object.duration !== null) {
message.duration = Duration.fromPartial(object.duration);
} else {
message.duration = undefined
}
if (object.arcs !== undefined && object.arcs !== null) {
for (const e of object.arcs) {
            message.arcs.push(ConfusionNetworkArc.fromPartial(e));
          }
}
return message;
}
          };

const baseConfusionNetworkArc: object = { word: "",confidence: 0 };

export const ConfusionNetworkArc = {
            encode(
      message: ConfusionNetworkArc,
      writer: Writer = Writer.create(),
    ): Writer {
if (message.word !== "") {
          writer.uint32(10).string(message.word);
        }
if (message.confidence !== 0) {
          writer.uint32(17).double(message.confidence);
        }
return writer;
},

decode(
      input: Reader | Uint8Array,
      length?: number,
    ): ConfusionNetworkArc {
      const reader = input instanceof Reader ? input : new Reader(input);
      let end = length === undefined ? reader.len : reader.pos + length;
      const message = { ...baseConfusionNetworkArc } as ConfusionNetworkArc;
while (reader.pos < end) {
      const tag = reader.uint32();
      switch (tag >>> 3) {
case 1:
message.word = reader.string();
break;
case 2:
message.confidence = reader.double();
break;
default:
      reader.skipType(tag & 7);
      break;
}
}
return message;
},

fromJSON(object: any): ConfusionNetworkArc {
      const message = { ...baseConfusionNetworkArc } as ConfusionNetworkArc;
if (object.word !== undefined && object.word !== null) {
message.word = String(object.word);
} else {
message.word = "";
}
if (object.confidence !== undefined && object.confidence !== null) {
message.confidence = Number(object.confidence);
} else {
message.confidence = 0;
}
return message
},

toJSON(message: ConfusionNetworkArc): unknown {
      const obj: any = {};
message.word !== undefined && (obj.word = message.word);
message.confidence !== undefined && (obj.confidence = message.confidence);
return obj;
},

fromPartial(object: DeepPartial<ConfusionNetworkArc>): ConfusionNetworkArc {
      const message = { ...baseConfusionNetworkArc } as ConfusionNetworkArc;
if (object.word !== undefined && object.word !== null) {
message.word = object.word;
} else {
message.word = ""
}
if (object.confidence !== undefined && object.confidence !== null) {
message.confidence = object.confidence;
} else {
message.confidence = 0
}
return message;
}
          };

/** Service that implements the Cobalt Cubic Speech Recognition API */
export interface Cubic {
/** Queries the Version of the Server */
Version(request: DeepPartial<Empty>,metadata?: grpc.Metadata): Promise<VersionResponse>;
/** Retrieves a list of available speech recognition models */
ListModels(request: DeepPartial<ListModelsRequest>,metadata?: grpc.Metadata): Promise<ListModelsResponse>;
/**
 * Performs synchronous speech recognition: receive results after all audio
 * has been sent and processed.  It is expected that this request be typically
 * used for short audio content: less than a minute long.  For longer content,
 * the `StreamingRecognize` method should be preferred.
 */
Recognize(request: DeepPartial<RecognizeRequest>,metadata?: grpc.Metadata): Promise<RecognitionResponse>;
/**
 * Performs bidirectional streaming speech recognition.  Receive results while
 * sending audio.  This method is only available via GRPC and not via
 * HTTP+JSON. However, a web browser may use websockets to use this service.
 */
StreamingRecognize(request: DeepPartial<Observable<StreamingRecognizeRequest>>,metadata?: grpc.Metadata): Observable<RecognitionResponse>;
/**
 * Compiles recognition context information, such as a specialized list of
 * words or phrases, into a compact, efficient form to send with subsequent
 * `Recognize` or `StreamingRecognize` requests to customize speech
 * recognition. For example, a list of contact names may be compiled in a
 * mobile app and sent with each recognition request so that the app user's
 * contact names are more likely to be recognized than arbitrary names. This
 * pre-compilation ensures that there is no added latency for the recognition
 * request. It is important to note that in order to compile context for a
 * model, that model has to support context in the first place, which can be
 * verified by checking its `ModelAttributes.ContextInfo` obtained via the
 * `ListModels` method. Also, the compiled data will be model specific; that
 * is, the data compiled for one model will generally not be usable with a
 * different model.
 */
CompileContext(request: DeepPartial<CompileContextRequest>,metadata?: grpc.Metadata): Promise<CompileContextResponse>;
}

export class CubicClientImpl implements Cubic {
  
    private readonly rpc: Rpc;
    
    constructor(rpc: Rpc) {
  this.rpc = rpc;this.Version = this.Version.bind(this);this.ListModels = this.ListModels.bind(this);this.Recognize = this.Recognize.bind(this);this.StreamingRecognize = this.StreamingRecognize.bind(this);this.CompileContext = this.CompileContext.bind(this);}

    Version(
      request: DeepPartial<Empty>,
      metadata?: grpc.Metadata,
    ): Promise<VersionResponse> {
      return this.rpc.unary(
        CubicVersionDesc,
        Empty.fromPartial(request),
        metadata,
      );
    }
  
    ListModels(
      request: DeepPartial<ListModelsRequest>,
      metadata?: grpc.Metadata,
    ): Promise<ListModelsResponse> {
      return this.rpc.unary(
        CubicListModelsDesc,
        ListModelsRequest.fromPartial(request),
        metadata,
      );
    }
  
    Recognize(
      request: DeepPartial<RecognizeRequest>,
      metadata?: grpc.Metadata,
    ): Promise<RecognitionResponse> {
      return this.rpc.unary(
        CubicRecognizeDesc,
        RecognizeRequest.fromPartial(request),
        metadata,
      );
    }
  
    StreamingRecognize(
      request: DeepPartial<Observable<StreamingRecognizeRequest>>,
      metadata?: grpc.Metadata,
    ): Observable<RecognitionResponse> {
      return this.rpc.invoke(
        CubicStreamingRecognizeDesc,
        Observable<StreamingRecognizeRequest>.fromPartial(request),
        metadata,
      );
    }
  
    CompileContext(
      request: DeepPartial<CompileContextRequest>,
      metadata?: grpc.Metadata,
    ): Promise<CompileContextResponse> {
      return this.rpc.unary(
        CubicCompileContextDesc,
        CompileContextRequest.fromPartial(request),
        metadata,
      );
    }
  }

export const CubicDesc = {
      serviceName: "cobaltspeech.cubic.Cubic",
    };

export const CubicVersionDesc: UnaryMethodDefinitionish = {
      methodName: "Version",
      service: CubicDesc,
      requestStream: false,
      responseStream: false,
      requestType: {
    serializeBinary() {
      return Empty.encode(this).finish();
    },
  } as any,
      responseType: {
    deserializeBinary(data: Uint8Array) {
      return { ...VersionResponse.decode(data), toObject() { return this; } };
    }
  } as any,
    };

export const CubicListModelsDesc: UnaryMethodDefinitionish = {
      methodName: "ListModels",
      service: CubicDesc,
      requestStream: false,
      responseStream: false,
      requestType: {
    serializeBinary() {
      return ListModelsRequest.encode(this).finish();
    },
  } as any,
      responseType: {
    deserializeBinary(data: Uint8Array) {
      return { ...ListModelsResponse.decode(data), toObject() { return this; } };
    }
  } as any,
    };

export const CubicRecognizeDesc: UnaryMethodDefinitionish = {
      methodName: "Recognize",
      service: CubicDesc,
      requestStream: false,
      responseStream: false,
      requestType: {
    serializeBinary() {
      return RecognizeRequest.encode(this).finish();
    },
  } as any,
      responseType: {
    deserializeBinary(data: Uint8Array) {
      return { ...RecognitionResponse.decode(data), toObject() { return this; } };
    }
  } as any,
    };

export const CubicStreamingRecognizeDesc: UnaryMethodDefinitionish = {
      methodName: "StreamingRecognize",
      service: CubicDesc,
      requestStream: false,
      responseStream: true,
      requestType: {
    serializeBinary() {
      return Observable<StreamingRecognizeRequest>.encode(this).finish();
    },
  } as any,
      responseType: {
    deserializeBinary(data: Uint8Array) {
      return { ...RecognitionResponse.decode(data), toObject() { return this; } };
    }
  } as any,
    };

export const CubicCompileContextDesc: UnaryMethodDefinitionish = {
      methodName: "CompileContext",
      service: CubicDesc,
      requestStream: false,
      responseStream: false,
      requestType: {
    serializeBinary() {
      return CompileContextRequest.encode(this).finish();
    },
  } as any,
      responseType: {
    deserializeBinary(data: Uint8Array) {
      return { ...CompileContextResponse.decode(data), toObject() { return this; } };
    }
  } as any,
    };

interface UnaryMethodDefinitionishR extends grpc.UnaryMethodDefinition<any, any> { requestStream: any; responseStream: any; }

type UnaryMethodDefinitionish = UnaryMethodDefinitionishR;

interface Rpc {
unary<T extends UnaryMethodDefinitionish>(
      methodDesc: T,
      request: any,
      metadata: grpc.Metadata | undefined,
    ): Promise<any>;
invoke<T extends UnaryMethodDefinitionish>(
        methodDesc: T,
        request: any,
        metadata: grpc.Metadata | undefined,
      ): Observable<any>;
}

export class GrpcWebImpl {
      private host: string;
      private options: 
    {
      transport?: grpc.TransportFactory,
      streamingTransport?: grpc.TransportFactory,
      debug?: boolean,
      metadata?: grpc.Metadata,
    }
  ;
      
      constructor(host: string, options: 
    {
      transport?: grpc.TransportFactory,
      streamingTransport?: grpc.TransportFactory,
      debug?: boolean,
      metadata?: grpc.Metadata,
    }
  ) {
        this.host = host;
        this.options = options;
      }
  
    unary<T extends UnaryMethodDefinitionish>(
      methodDesc: T,
      _request: any,
      metadata: grpc.Metadata | undefined
    ): Promise<any> {
      const request = { ..._request, ...methodDesc.requestType };
      const maybeCombinedMetadata =
        metadata && this.options.metadata
          ? new BrowserHeaders({ ...this.options?.metadata.headersMap, ...metadata?.headersMap })
          : metadata || this.options.metadata;
      return new Promise((resolve, reject) => {
      grpc.unary(methodDesc, {
          request,
          host: this.host,
          metadata: maybeCombinedMetadata,
          transport: this.options.transport,
          debug: this.options.debug,
          onEnd: function (response) {
            if (response.status === grpc.Code.OK) {
              resolve(response.message);
            } else {
              const err = new Error(response.statusMessage) as any;
              err.code = response.status;
              err.metadata = response.trailers;
              reject(err);
            }
          },
        });
      });
    }
  
    invoke<T extends UnaryMethodDefinitionish>(
      methodDesc: T,
      _request: any,
      metadata: grpc.Metadata | undefined
    ): Observable<any> {
      // Status Response Codes (https://developers.google.com/maps-booking/reference/grpc-api/status_codes)
      const upStreamCodes = [2, 4, 8, 9, 10, 13, 14, 15]; 
      const DEFAULT_TIMEOUT_TIME: number = 3_000;
      const request = { ..._request, ...methodDesc.requestType };
      const maybeCombinedMetadata =
      metadata && this.options.metadata
        ? new BrowserHeaders({ ...this.options?.metadata.headersMap, ...metadata?.headersMap })
        : metadata || this.options.metadata;
      return new Observable(observer => {
        const upStream = (() => {
          grpc.invoke(methodDesc, {
            host: this.host,
            request,
            transport: this.options.streamingTransport || this.options.transport,
            metadata: maybeCombinedMetadata,
            debug: this.options.debug,
            onMessage: (next) => observer.next(next),
            onEnd: (code: grpc.Code, message: string) => {
              if (code === 0) {
                observer.complete();
              } else if (upStreamCodes.includes(code)) {
                setTimeout(upStream, DEFAULT_TIMEOUT_TIME);
              } else {
                observer.error(new Error(`Error ${code} ${message}`));
              }
            },
          });
        });
        upStream();
      }).pipe(share());
    }
  }

declare var self: any | undefined;
      declare var window: any | undefined;
      var globalThis: any = (() => {
        if (typeof globalThis !== "undefined") return globalThis;
        if (typeof self !== "undefined") return self;
        if (typeof window !== "undefined") return window;
        if (typeof global !== "undefined") return global;
        throw "Unable to locate global object";
      })();

const atob: (b64: string) => string = globalThis.atob || ((b64) => globalThis.Buffer.from(b64, 'base64').toString('binary'));
      function bytesFromBase64(b64: string): Uint8Array {
        const bin = atob(b64);
        const arr = new Uint8Array(bin.length);
        for (let i = 0; i < bin.length; ++i) {
            arr[i] = bin.charCodeAt(i);
        }
        return arr;
      }

const btoa : (bin: string) => string = globalThis.btoa || ((bin) => globalThis.Buffer.from(bin, 'binary').toString('base64'));
      function base64FromBytes(arr: Uint8Array): string {
        const bin: string[] = [];
        for (let i = 0; i < arr.byteLength; ++i) {
          bin.push(String.fromCharCode(arr[i]));
        }
        return btoa(bin.join(''));
      }

type Builtin = Date | Function | Uint8Array | string | number | boolean | undefined;
      export type DeepPartial<T> = T extends Builtin
        ? T
        : T extends Array<infer U>
        ? Array<DeepPartial<U>>
        : T extends ReadonlyArray<infer U>
        ? ReadonlyArray<DeepPartial<U>>
        : T extends {}
        ? { [K in keyof T]?: DeepPartial<T[K]> }
        : Partial<T>;













// If you get a compile-error about 'Constructor<Long> and ... have no overlap',
    // add '--ts_proto_opt=esModuleInterop=true' as a flag when calling 'protoc'.
      if (util.Long !== Long) {
        util.Long = Long as any;
        configure();
      }

