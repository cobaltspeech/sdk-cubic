// package: cobaltspeech.cubic
// file: cubic.proto

import * as jspb from "google-protobuf";
import * as google_api_annotations_pb from "./google/api/annotations_pb";
import * as google_protobuf_duration_pb from "google-protobuf/google/protobuf/duration_pb";
import * as google_protobuf_empty_pb from "google-protobuf/google/protobuf/empty_pb";

export class ListModelsRequest extends jspb.Message {
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListModelsRequest.AsObject;
  static toObject(includeInstance: boolean, msg: ListModelsRequest): ListModelsRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ListModelsRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListModelsRequest;
  static deserializeBinaryFromReader(message: ListModelsRequest, reader: jspb.BinaryReader): ListModelsRequest;
}

export namespace ListModelsRequest {
  export type AsObject = {
  }
}

export class RecognizeRequest extends jspb.Message {
  hasConfig(): boolean;
  clearConfig(): void;
  getConfig(): RecognitionConfig | undefined;
  setConfig(value?: RecognitionConfig): void;

  hasAudio(): boolean;
  clearAudio(): void;
  getAudio(): RecognitionAudio | undefined;
  setAudio(value?: RecognitionAudio): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecognizeRequest.AsObject;
  static toObject(includeInstance: boolean, msg: RecognizeRequest): RecognizeRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecognizeRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecognizeRequest;
  static deserializeBinaryFromReader(message: RecognizeRequest, reader: jspb.BinaryReader): RecognizeRequest;
}

export namespace RecognizeRequest {
  export type AsObject = {
    config?: RecognitionConfig.AsObject,
    audio?: RecognitionAudio.AsObject,
  }
}

export class StreamingRecognizeRequest extends jspb.Message {
  hasConfig(): boolean;
  clearConfig(): void;
  getConfig(): RecognitionConfig | undefined;
  setConfig(value?: RecognitionConfig): void;

  hasAudio(): boolean;
  clearAudio(): void;
  getAudio(): RecognitionAudio | undefined;
  setAudio(value?: RecognitionAudio): void;

  getRequestCase(): StreamingRecognizeRequest.RequestCase;
  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): StreamingRecognizeRequest.AsObject;
  static toObject(includeInstance: boolean, msg: StreamingRecognizeRequest): StreamingRecognizeRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: StreamingRecognizeRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): StreamingRecognizeRequest;
  static deserializeBinaryFromReader(message: StreamingRecognizeRequest, reader: jspb.BinaryReader): StreamingRecognizeRequest;
}

export namespace StreamingRecognizeRequest {
  export type AsObject = {
    config?: RecognitionConfig.AsObject,
    audio?: RecognitionAudio.AsObject,
  }

  export enum RequestCase {
    REQUEST_NOT_SET = 0,
    CONFIG = 1,
    AUDIO = 2,
  }
}

export class CompileContextRequest extends jspb.Message {
  getModelId(): string;
  setModelId(value: string): void;

  getToken(): string;
  setToken(value: string): void;

  clearPhrasesList(): void;
  getPhrasesList(): Array<ContextPhrase>;
  setPhrasesList(value: Array<ContextPhrase>): void;
  addPhrases(value?: ContextPhrase, index?: number): ContextPhrase;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CompileContextRequest.AsObject;
  static toObject(includeInstance: boolean, msg: CompileContextRequest): CompileContextRequest.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: CompileContextRequest, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CompileContextRequest;
  static deserializeBinaryFromReader(message: CompileContextRequest, reader: jspb.BinaryReader): CompileContextRequest;
}

export namespace CompileContextRequest {
  export type AsObject = {
    modelId: string,
    token: string,
    phrasesList: Array<ContextPhrase.AsObject>,
  }
}

export class VersionResponse extends jspb.Message {
  getCubic(): string;
  setCubic(value: string): void;

  getServer(): string;
  setServer(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): VersionResponse.AsObject;
  static toObject(includeInstance: boolean, msg: VersionResponse): VersionResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: VersionResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): VersionResponse;
  static deserializeBinaryFromReader(message: VersionResponse, reader: jspb.BinaryReader): VersionResponse;
}

export namespace VersionResponse {
  export type AsObject = {
    cubic: string,
    server: string,
  }
}

export class ListModelsResponse extends jspb.Message {
  clearModelsList(): void;
  getModelsList(): Array<Model>;
  setModelsList(value: Array<Model>): void;
  addModels(value?: Model, index?: number): Model;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ListModelsResponse.AsObject;
  static toObject(includeInstance: boolean, msg: ListModelsResponse): ListModelsResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ListModelsResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ListModelsResponse;
  static deserializeBinaryFromReader(message: ListModelsResponse, reader: jspb.BinaryReader): ListModelsResponse;
}

export namespace ListModelsResponse {
  export type AsObject = {
    modelsList: Array<Model.AsObject>,
  }
}

export class RecognitionResponse extends jspb.Message {
  clearResultsList(): void;
  getResultsList(): Array<RecognitionResult>;
  setResultsList(value: Array<RecognitionResult>): void;
  addResults(value?: RecognitionResult, index?: number): RecognitionResult;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecognitionResponse.AsObject;
  static toObject(includeInstance: boolean, msg: RecognitionResponse): RecognitionResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecognitionResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecognitionResponse;
  static deserializeBinaryFromReader(message: RecognitionResponse, reader: jspb.BinaryReader): RecognitionResponse;
}

export namespace RecognitionResponse {
  export type AsObject = {
    resultsList: Array<RecognitionResult.AsObject>,
  }
}

export class CompileContextResponse extends jspb.Message {
  hasContext(): boolean;
  clearContext(): void;
  getContext(): CompiledContext | undefined;
  setContext(value?: CompiledContext): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CompileContextResponse.AsObject;
  static toObject(includeInstance: boolean, msg: CompileContextResponse): CompileContextResponse.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: CompileContextResponse, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CompileContextResponse;
  static deserializeBinaryFromReader(message: CompileContextResponse, reader: jspb.BinaryReader): CompileContextResponse;
}

export namespace CompileContextResponse {
  export type AsObject = {
    context?: CompiledContext.AsObject,
  }
}

export class RecognitionConfig extends jspb.Message {
  getModelId(): string;
  setModelId(value: string): void;

  getAudioEncoding(): RecognitionConfig.EncodingMap[keyof RecognitionConfig.EncodingMap];
  setAudioEncoding(value: RecognitionConfig.EncodingMap[keyof RecognitionConfig.EncodingMap]): void;

  hasIdleTimeout(): boolean;
  clearIdleTimeout(): void;
  getIdleTimeout(): google_protobuf_duration_pb.Duration | undefined;
  setIdleTimeout(value?: google_protobuf_duration_pb.Duration): void;

  getEnableWordTimeOffsets(): boolean;
  setEnableWordTimeOffsets(value: boolean): void;

  getEnableWordConfidence(): boolean;
  setEnableWordConfidence(value: boolean): void;

  getEnableRawTranscript(): boolean;
  setEnableRawTranscript(value: boolean): void;

  getEnableConfusionNetwork(): boolean;
  setEnableConfusionNetwork(value: boolean): void;

  clearAudioChannelsList(): void;
  getAudioChannelsList(): Array<number>;
  setAudioChannelsList(value: Array<number>): void;
  addAudioChannels(value: number, index?: number): number;

  hasMetadata(): boolean;
  clearMetadata(): void;
  getMetadata(): RecognitionMetadata | undefined;
  setMetadata(value?: RecognitionMetadata): void;

  hasContext(): boolean;
  clearContext(): void;
  getContext(): RecognitionContext | undefined;
  setContext(value?: RecognitionContext): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecognitionConfig.AsObject;
  static toObject(includeInstance: boolean, msg: RecognitionConfig): RecognitionConfig.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecognitionConfig, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecognitionConfig;
  static deserializeBinaryFromReader(message: RecognitionConfig, reader: jspb.BinaryReader): RecognitionConfig;
}

export namespace RecognitionConfig {
  export type AsObject = {
    modelId: string,
    audioEncoding: RecognitionConfig.EncodingMap[keyof RecognitionConfig.EncodingMap],
    idleTimeout?: google_protobuf_duration_pb.Duration.AsObject,
    enableWordTimeOffsets: boolean,
    enableWordConfidence: boolean,
    enableRawTranscript: boolean,
    enableConfusionNetwork: boolean,
    audioChannelsList: Array<number>,
    metadata?: RecognitionMetadata.AsObject,
    context?: RecognitionContext.AsObject,
  }

  export interface EncodingMap {
    RAW_LINEAR16: 0;
    WAV: 1;
    MP3: 2;
    FLAC: 3;
    VOX8000: 4;
    ULAW8000: 5;
  }

  export const Encoding: EncodingMap;
}

export class RecognitionMetadata extends jspb.Message {
  getCustomMetadata(): string;
  setCustomMetadata(value: string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecognitionMetadata.AsObject;
  static toObject(includeInstance: boolean, msg: RecognitionMetadata): RecognitionMetadata.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecognitionMetadata, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecognitionMetadata;
  static deserializeBinaryFromReader(message: RecognitionMetadata, reader: jspb.BinaryReader): RecognitionMetadata;
}

export namespace RecognitionMetadata {
  export type AsObject = {
    customMetadata: string,
  }
}

export class RecognitionContext extends jspb.Message {
  clearCompiledList(): void;
  getCompiledList(): Array<CompiledContext>;
  setCompiledList(value: Array<CompiledContext>): void;
  addCompiled(value?: CompiledContext, index?: number): CompiledContext;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecognitionContext.AsObject;
  static toObject(includeInstance: boolean, msg: RecognitionContext): RecognitionContext.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecognitionContext, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecognitionContext;
  static deserializeBinaryFromReader(message: RecognitionContext, reader: jspb.BinaryReader): RecognitionContext;
}

export namespace RecognitionContext {
  export type AsObject = {
    compiledList: Array<CompiledContext.AsObject>,
  }
}

export class CompiledContext extends jspb.Message {
  getData(): Uint8Array | string;
  getData_asU8(): Uint8Array;
  getData_asB64(): string;
  setData(value: Uint8Array | string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): CompiledContext.AsObject;
  static toObject(includeInstance: boolean, msg: CompiledContext): CompiledContext.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: CompiledContext, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): CompiledContext;
  static deserializeBinaryFromReader(message: CompiledContext, reader: jspb.BinaryReader): CompiledContext;
}

export namespace CompiledContext {
  export type AsObject = {
    data: Uint8Array | string,
  }
}

export class ContextPhrase extends jspb.Message {
  getText(): string;
  setText(value: string): void;

  getBoost(): number;
  setBoost(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ContextPhrase.AsObject;
  static toObject(includeInstance: boolean, msg: ContextPhrase): ContextPhrase.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ContextPhrase, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ContextPhrase;
  static deserializeBinaryFromReader(message: ContextPhrase, reader: jspb.BinaryReader): ContextPhrase;
}

export namespace ContextPhrase {
  export type AsObject = {
    text: string,
    boost: number,
  }
}

export class RecognitionAudio extends jspb.Message {
  getData(): Uint8Array | string;
  getData_asU8(): Uint8Array;
  getData_asB64(): string;
  setData(value: Uint8Array | string): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecognitionAudio.AsObject;
  static toObject(includeInstance: boolean, msg: RecognitionAudio): RecognitionAudio.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecognitionAudio, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecognitionAudio;
  static deserializeBinaryFromReader(message: RecognitionAudio, reader: jspb.BinaryReader): RecognitionAudio;
}

export namespace RecognitionAudio {
  export type AsObject = {
    data: Uint8Array | string,
  }
}

export class Model extends jspb.Message {
  getId(): string;
  setId(value: string): void;

  getName(): string;
  setName(value: string): void;

  hasAttributes(): boolean;
  clearAttributes(): void;
  getAttributes(): ModelAttributes | undefined;
  setAttributes(value?: ModelAttributes): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): Model.AsObject;
  static toObject(includeInstance: boolean, msg: Model): Model.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: Model, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): Model;
  static deserializeBinaryFromReader(message: Model, reader: jspb.BinaryReader): Model;
}

export namespace Model {
  export type AsObject = {
    id: string,
    name: string,
    attributes?: ModelAttributes.AsObject,
  }
}

export class ModelAttributes extends jspb.Message {
  getSampleRate(): number;
  setSampleRate(value: number): void;

  hasContextInfo(): boolean;
  clearContextInfo(): void;
  getContextInfo(): ContextInfo | undefined;
  setContextInfo(value?: ContextInfo): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ModelAttributes.AsObject;
  static toObject(includeInstance: boolean, msg: ModelAttributes): ModelAttributes.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ModelAttributes, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ModelAttributes;
  static deserializeBinaryFromReader(message: ModelAttributes, reader: jspb.BinaryReader): ModelAttributes;
}

export namespace ModelAttributes {
  export type AsObject = {
    sampleRate: number,
    contextInfo?: ContextInfo.AsObject,
  }
}

export class ContextInfo extends jspb.Message {
  getSupportsContext(): boolean;
  setSupportsContext(value: boolean): void;

  clearAllowedContextTokensList(): void;
  getAllowedContextTokensList(): Array<string>;
  setAllowedContextTokensList(value: Array<string>): void;
  addAllowedContextTokens(value: string, index?: number): string;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ContextInfo.AsObject;
  static toObject(includeInstance: boolean, msg: ContextInfo): ContextInfo.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ContextInfo, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ContextInfo;
  static deserializeBinaryFromReader(message: ContextInfo, reader: jspb.BinaryReader): ContextInfo;
}

export namespace ContextInfo {
  export type AsObject = {
    supportsContext: boolean,
    allowedContextTokensList: Array<string>,
  }
}

export class RecognitionResult extends jspb.Message {
  clearAlternativesList(): void;
  getAlternativesList(): Array<RecognitionAlternative>;
  setAlternativesList(value: Array<RecognitionAlternative>): void;
  addAlternatives(value?: RecognitionAlternative, index?: number): RecognitionAlternative;

  getIsPartial(): boolean;
  setIsPartial(value: boolean): void;

  hasCnet(): boolean;
  clearCnet(): void;
  getCnet(): RecognitionConfusionNetwork | undefined;
  setCnet(value?: RecognitionConfusionNetwork): void;

  getAudioChannel(): number;
  setAudioChannel(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecognitionResult.AsObject;
  static toObject(includeInstance: boolean, msg: RecognitionResult): RecognitionResult.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecognitionResult, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecognitionResult;
  static deserializeBinaryFromReader(message: RecognitionResult, reader: jspb.BinaryReader): RecognitionResult;
}

export namespace RecognitionResult {
  export type AsObject = {
    alternativesList: Array<RecognitionAlternative.AsObject>,
    isPartial: boolean,
    cnet?: RecognitionConfusionNetwork.AsObject,
    audioChannel: number,
  }
}

export class RecognitionAlternative extends jspb.Message {
  getTranscript(): string;
  setTranscript(value: string): void;

  getRawTranscript(): string;
  setRawTranscript(value: string): void;

  getConfidence(): number;
  setConfidence(value: number): void;

  clearWordsList(): void;
  getWordsList(): Array<WordInfo>;
  setWordsList(value: Array<WordInfo>): void;
  addWords(value?: WordInfo, index?: number): WordInfo;

  clearRawWordsList(): void;
  getRawWordsList(): Array<WordInfo>;
  setRawWordsList(value: Array<WordInfo>): void;
  addRawWords(value?: WordInfo, index?: number): WordInfo;

  hasStartTime(): boolean;
  clearStartTime(): void;
  getStartTime(): google_protobuf_duration_pb.Duration | undefined;
  setStartTime(value?: google_protobuf_duration_pb.Duration): void;

  hasDuration(): boolean;
  clearDuration(): void;
  getDuration(): google_protobuf_duration_pb.Duration | undefined;
  setDuration(value?: google_protobuf_duration_pb.Duration): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecognitionAlternative.AsObject;
  static toObject(includeInstance: boolean, msg: RecognitionAlternative): RecognitionAlternative.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecognitionAlternative, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecognitionAlternative;
  static deserializeBinaryFromReader(message: RecognitionAlternative, reader: jspb.BinaryReader): RecognitionAlternative;
}

export namespace RecognitionAlternative {
  export type AsObject = {
    transcript: string,
    rawTranscript: string,
    confidence: number,
    wordsList: Array<WordInfo.AsObject>,
    rawWordsList: Array<WordInfo.AsObject>,
    startTime?: google_protobuf_duration_pb.Duration.AsObject,
    duration?: google_protobuf_duration_pb.Duration.AsObject,
  }
}

export class WordInfo extends jspb.Message {
  getWord(): string;
  setWord(value: string): void;

  getConfidence(): number;
  setConfidence(value: number): void;

  hasStartTime(): boolean;
  clearStartTime(): void;
  getStartTime(): google_protobuf_duration_pb.Duration | undefined;
  setStartTime(value?: google_protobuf_duration_pb.Duration): void;

  hasDuration(): boolean;
  clearDuration(): void;
  getDuration(): google_protobuf_duration_pb.Duration | undefined;
  setDuration(value?: google_protobuf_duration_pb.Duration): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): WordInfo.AsObject;
  static toObject(includeInstance: boolean, msg: WordInfo): WordInfo.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: WordInfo, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): WordInfo;
  static deserializeBinaryFromReader(message: WordInfo, reader: jspb.BinaryReader): WordInfo;
}

export namespace WordInfo {
  export type AsObject = {
    word: string,
    confidence: number,
    startTime?: google_protobuf_duration_pb.Duration.AsObject,
    duration?: google_protobuf_duration_pb.Duration.AsObject,
  }
}

export class RecognitionConfusionNetwork extends jspb.Message {
  clearLinksList(): void;
  getLinksList(): Array<ConfusionNetworkLink>;
  setLinksList(value: Array<ConfusionNetworkLink>): void;
  addLinks(value?: ConfusionNetworkLink, index?: number): ConfusionNetworkLink;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): RecognitionConfusionNetwork.AsObject;
  static toObject(includeInstance: boolean, msg: RecognitionConfusionNetwork): RecognitionConfusionNetwork.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: RecognitionConfusionNetwork, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): RecognitionConfusionNetwork;
  static deserializeBinaryFromReader(message: RecognitionConfusionNetwork, reader: jspb.BinaryReader): RecognitionConfusionNetwork;
}

export namespace RecognitionConfusionNetwork {
  export type AsObject = {
    linksList: Array<ConfusionNetworkLink.AsObject>,
  }
}

export class ConfusionNetworkLink extends jspb.Message {
  hasStartTime(): boolean;
  clearStartTime(): void;
  getStartTime(): google_protobuf_duration_pb.Duration | undefined;
  setStartTime(value?: google_protobuf_duration_pb.Duration): void;

  hasDuration(): boolean;
  clearDuration(): void;
  getDuration(): google_protobuf_duration_pb.Duration | undefined;
  setDuration(value?: google_protobuf_duration_pb.Duration): void;

  clearArcsList(): void;
  getArcsList(): Array<ConfusionNetworkArc>;
  setArcsList(value: Array<ConfusionNetworkArc>): void;
  addArcs(value?: ConfusionNetworkArc, index?: number): ConfusionNetworkArc;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ConfusionNetworkLink.AsObject;
  static toObject(includeInstance: boolean, msg: ConfusionNetworkLink): ConfusionNetworkLink.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ConfusionNetworkLink, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ConfusionNetworkLink;
  static deserializeBinaryFromReader(message: ConfusionNetworkLink, reader: jspb.BinaryReader): ConfusionNetworkLink;
}

export namespace ConfusionNetworkLink {
  export type AsObject = {
    startTime?: google_protobuf_duration_pb.Duration.AsObject,
    duration?: google_protobuf_duration_pb.Duration.AsObject,
    arcsList: Array<ConfusionNetworkArc.AsObject>,
  }
}

export class ConfusionNetworkArc extends jspb.Message {
  getWord(): string;
  setWord(value: string): void;

  getConfidence(): number;
  setConfidence(value: number): void;

  serializeBinary(): Uint8Array;
  toObject(includeInstance?: boolean): ConfusionNetworkArc.AsObject;
  static toObject(includeInstance: boolean, msg: ConfusionNetworkArc): ConfusionNetworkArc.AsObject;
  static extensions: {[key: number]: jspb.ExtensionFieldInfo<jspb.Message>};
  static extensionsBinary: {[key: number]: jspb.ExtensionFieldBinaryInfo<jspb.Message>};
  static serializeBinaryToWriter(message: ConfusionNetworkArc, writer: jspb.BinaryWriter): void;
  static deserializeBinary(bytes: Uint8Array): ConfusionNetworkArc;
  static deserializeBinaryFromReader(message: ConfusionNetworkArc, reader: jspb.BinaryReader): ConfusionNetworkArc;
}

export namespace ConfusionNetworkArc {
  export type AsObject = {
    word: string,
    confidence: number,
  }
}

