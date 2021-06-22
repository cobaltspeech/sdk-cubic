// package: cobaltspeech.cubic
// file: cubic.proto

import * as cubic_pb from "./cubic_pb";
import * as google_protobuf_empty_pb from "google-protobuf/google/protobuf/empty_pb";
import {grpc} from "@improbable-eng/grpc-web";

type CubicVersion = {
  readonly methodName: string;
  readonly service: typeof Cubic;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof google_protobuf_empty_pb.Empty;
  readonly responseType: typeof cubic_pb.VersionResponse;
};

type CubicListModels = {
  readonly methodName: string;
  readonly service: typeof Cubic;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof cubic_pb.ListModelsRequest;
  readonly responseType: typeof cubic_pb.ListModelsResponse;
};

type CubicRecognize = {
  readonly methodName: string;
  readonly service: typeof Cubic;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof cubic_pb.RecognizeRequest;
  readonly responseType: typeof cubic_pb.RecognitionResponse;
};

type CubicStreamingRecognize = {
  readonly methodName: string;
  readonly service: typeof Cubic;
  readonly requestStream: true;
  readonly responseStream: true;
  readonly requestType: typeof cubic_pb.StreamingRecognizeRequest;
  readonly responseType: typeof cubic_pb.RecognitionResponse;
};

type CubicCompileContext = {
  readonly methodName: string;
  readonly service: typeof Cubic;
  readonly requestStream: false;
  readonly responseStream: false;
  readonly requestType: typeof cubic_pb.CompileContextRequest;
  readonly responseType: typeof cubic_pb.CompileContextResponse;
};

export class Cubic {
  static readonly serviceName: string;
  static readonly Version: CubicVersion;
  static readonly ListModels: CubicListModels;
  static readonly Recognize: CubicRecognize;
  static readonly StreamingRecognize: CubicStreamingRecognize;
  static readonly CompileContext: CubicCompileContext;
}

export type ServiceError = { message: string, code: number; metadata: grpc.Metadata }
export type Status = { details: string, code: number; metadata: grpc.Metadata }

interface UnaryResponse {
  cancel(): void;
}
interface ResponseStream<T> {
  cancel(): void;
  on(type: 'data', handler: (message: T) => void): ResponseStream<T>;
  on(type: 'end', handler: (status?: Status) => void): ResponseStream<T>;
  on(type: 'status', handler: (status: Status) => void): ResponseStream<T>;
}
interface RequestStream<T> {
  write(message: T): RequestStream<T>;
  end(): void;
  cancel(): void;
  on(type: 'end', handler: (status?: Status) => void): RequestStream<T>;
  on(type: 'status', handler: (status: Status) => void): RequestStream<T>;
}
interface BidirectionalStream<ReqT, ResT> {
  write(message: ReqT): BidirectionalStream<ReqT, ResT>;
  end(): void;
  cancel(): void;
  on(type: 'data', handler: (message: ResT) => void): BidirectionalStream<ReqT, ResT>;
  on(type: 'end', handler: (status?: Status) => void): BidirectionalStream<ReqT, ResT>;
  on(type: 'status', handler: (status: Status) => void): BidirectionalStream<ReqT, ResT>;
}

export class CubicClient {
  readonly serviceHost: string;

  constructor(serviceHost: string, options?: grpc.RpcOptions);
  version(
    requestMessage: google_protobuf_empty_pb.Empty,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: cubic_pb.VersionResponse|null) => void
  ): UnaryResponse;
  version(
    requestMessage: google_protobuf_empty_pb.Empty,
    callback: (error: ServiceError|null, responseMessage: cubic_pb.VersionResponse|null) => void
  ): UnaryResponse;
  listModels(
    requestMessage: cubic_pb.ListModelsRequest,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: cubic_pb.ListModelsResponse|null) => void
  ): UnaryResponse;
  listModels(
    requestMessage: cubic_pb.ListModelsRequest,
    callback: (error: ServiceError|null, responseMessage: cubic_pb.ListModelsResponse|null) => void
  ): UnaryResponse;
  recognize(
    requestMessage: cubic_pb.RecognizeRequest,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: cubic_pb.RecognitionResponse|null) => void
  ): UnaryResponse;
  recognize(
    requestMessage: cubic_pb.RecognizeRequest,
    callback: (error: ServiceError|null, responseMessage: cubic_pb.RecognitionResponse|null) => void
  ): UnaryResponse;
  streamingRecognize(metadata?: grpc.Metadata): BidirectionalStream<cubic_pb.StreamingRecognizeRequest, cubic_pb.RecognitionResponse>;
  compileContext(
    requestMessage: cubic_pb.CompileContextRequest,
    metadata: grpc.Metadata,
    callback: (error: ServiceError|null, responseMessage: cubic_pb.CompileContextResponse|null) => void
  ): UnaryResponse;
  compileContext(
    requestMessage: cubic_pb.CompileContextRequest,
    callback: (error: ServiceError|null, responseMessage: cubic_pb.CompileContextResponse|null) => void
  ): UnaryResponse;
}

