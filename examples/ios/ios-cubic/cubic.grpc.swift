//
// DO NOT EDIT.
//
// Generated by the protocol buffer compiler.
// Source: swift-gen/cubic.proto
//

//
// Copyright 2018, gRPC Authors All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
import Dispatch
import Foundation
import SwiftGRPC
import SwiftProtobuf

internal protocol Cobaltspeech_Cubic_CubicVersionCall: ClientCallUnary {}

fileprivate final class Cobaltspeech_Cubic_CubicVersionCallBase: ClientCallUnaryBase<SwiftProtobuf.Google_Protobuf_Empty, Cobaltspeech_Cubic_VersionResponse>, Cobaltspeech_Cubic_CubicVersionCall {
  override class var method: String { return "/cobaltspeech.cubic.Cubic/Version" }
}

internal protocol Cobaltspeech_Cubic_CubicListModelsCall: ClientCallUnary {}

fileprivate final class Cobaltspeech_Cubic_CubicListModelsCallBase: ClientCallUnaryBase<Cobaltspeech_Cubic_ListModelsRequest, Cobaltspeech_Cubic_ListModelsResponse>, Cobaltspeech_Cubic_CubicListModelsCall {
  override class var method: String { return "/cobaltspeech.cubic.Cubic/ListModels" }
}

internal protocol Cobaltspeech_Cubic_CubicRecognizeCall: ClientCallUnary {}

fileprivate final class Cobaltspeech_Cubic_CubicRecognizeCallBase: ClientCallUnaryBase<Cobaltspeech_Cubic_RecognizeRequest, Cobaltspeech_Cubic_RecognitionResponse>, Cobaltspeech_Cubic_CubicRecognizeCall {
  override class var method: String { return "/cobaltspeech.cubic.Cubic/Recognize" }
}

internal protocol Cobaltspeech_Cubic_CubicStreamingRecognizeCall: ClientCallBidirectionalStreaming {
  /// Do not call this directly, call `receive()` in the protocol extension below instead.
  func _receive(timeout: DispatchTime) throws -> Cobaltspeech_Cubic_RecognitionResponse?
  /// Call this to wait for a result. Nonblocking.
  func receive(completion: @escaping (ResultOrRPCError<Cobaltspeech_Cubic_RecognitionResponse?>) -> Void) throws

  /// Send a message to the stream. Nonblocking.
  func send(_ message: Cobaltspeech_Cubic_StreamingRecognizeRequest, completion: @escaping (Error?) -> Void) throws
  /// Do not call this directly, call `send()` in the protocol extension below instead.
  func _send(_ message: Cobaltspeech_Cubic_StreamingRecognizeRequest, timeout: DispatchTime) throws

  /// Call this to close the sending connection. Blocking.
  func closeSend() throws
  /// Call this to close the sending connection. Nonblocking.
  func closeSend(completion: (() -> Void)?) throws
}

internal extension Cobaltspeech_Cubic_CubicStreamingRecognizeCall {
  /// Call this to wait for a result. Blocking.
  func receive(timeout: DispatchTime = .distantFuture) throws -> Cobaltspeech_Cubic_RecognitionResponse? { return try self._receive(timeout: timeout) }
}

internal extension Cobaltspeech_Cubic_CubicStreamingRecognizeCall {
  /// Send a message to the stream and wait for the send operation to finish. Blocking.
  func send(_ message: Cobaltspeech_Cubic_StreamingRecognizeRequest, timeout: DispatchTime = .distantFuture) throws { try self._send(message, timeout: timeout) }
}

fileprivate final class Cobaltspeech_Cubic_CubicStreamingRecognizeCallBase: ClientCallBidirectionalStreamingBase<Cobaltspeech_Cubic_StreamingRecognizeRequest, Cobaltspeech_Cubic_RecognitionResponse>, Cobaltspeech_Cubic_CubicStreamingRecognizeCall {
  override class var method: String { return "/cobaltspeech.cubic.Cubic/StreamingRecognize" }
}


/// Instantiate Cobaltspeech_Cubic_CubicServiceClient, then call methods of this protocol to make API calls.
internal protocol Cobaltspeech_Cubic_CubicService: ServiceClient {
  /// Synchronous. Unary.
  func version(_ request: SwiftProtobuf.Google_Protobuf_Empty, metadata customMetadata: Metadata) throws -> Cobaltspeech_Cubic_VersionResponse
  /// Asynchronous. Unary.
  @discardableResult
  func version(_ request: SwiftProtobuf.Google_Protobuf_Empty, metadata customMetadata: Metadata, completion: @escaping (Cobaltspeech_Cubic_VersionResponse?, CallResult) -> Void) throws -> Cobaltspeech_Cubic_CubicVersionCall

  /// Synchronous. Unary.
  func listModels(_ request: Cobaltspeech_Cubic_ListModelsRequest, metadata customMetadata: Metadata) throws -> Cobaltspeech_Cubic_ListModelsResponse
  /// Asynchronous. Unary.
  @discardableResult
  func listModels(_ request: Cobaltspeech_Cubic_ListModelsRequest, metadata customMetadata: Metadata, completion: @escaping (Cobaltspeech_Cubic_ListModelsResponse?, CallResult) -> Void) throws -> Cobaltspeech_Cubic_CubicListModelsCall

  /// Synchronous. Unary.
  func recognize(_ request: Cobaltspeech_Cubic_RecognizeRequest, metadata customMetadata: Metadata) throws -> Cobaltspeech_Cubic_RecognitionResponse
  /// Asynchronous. Unary.
  @discardableResult
  func recognize(_ request: Cobaltspeech_Cubic_RecognizeRequest, metadata customMetadata: Metadata, completion: @escaping (Cobaltspeech_Cubic_RecognitionResponse?, CallResult) -> Void) throws -> Cobaltspeech_Cubic_CubicRecognizeCall

  /// Asynchronous. Bidirectional-streaming.
  /// Use methods on the returned object to stream messages,
  /// to wait for replies, and to close the connection.
  func streamingRecognize(metadata customMetadata: Metadata, completion: ((CallResult) -> Void)?) throws -> Cobaltspeech_Cubic_CubicStreamingRecognizeCall

}

internal extension Cobaltspeech_Cubic_CubicService {
  /// Synchronous. Unary.
  func version(_ request: SwiftProtobuf.Google_Protobuf_Empty) throws -> Cobaltspeech_Cubic_VersionResponse {
    return try self.version(request, metadata: self.metadata)
  }
  /// Asynchronous. Unary.
  @discardableResult
  func version(_ request: SwiftProtobuf.Google_Protobuf_Empty, completion: @escaping (Cobaltspeech_Cubic_VersionResponse?, CallResult) -> Void) throws -> Cobaltspeech_Cubic_CubicVersionCall {
    return try self.version(request, metadata: self.metadata, completion: completion)
  }

  /// Synchronous. Unary.
  func listModels(_ request: Cobaltspeech_Cubic_ListModelsRequest) throws -> Cobaltspeech_Cubic_ListModelsResponse {
    return try self.listModels(request, metadata: self.metadata)
  }
  /// Asynchronous. Unary.
  @discardableResult
  func listModels(_ request: Cobaltspeech_Cubic_ListModelsRequest, completion: @escaping (Cobaltspeech_Cubic_ListModelsResponse?, CallResult) -> Void) throws -> Cobaltspeech_Cubic_CubicListModelsCall {
    return try self.listModels(request, metadata: self.metadata, completion: completion)
  }

  /// Synchronous. Unary.
  func recognize(_ request: Cobaltspeech_Cubic_RecognizeRequest) throws -> Cobaltspeech_Cubic_RecognitionResponse {
    return try self.recognize(request, metadata: self.metadata)
  }
  /// Asynchronous. Unary.
  @discardableResult
  func recognize(_ request: Cobaltspeech_Cubic_RecognizeRequest, completion: @escaping (Cobaltspeech_Cubic_RecognitionResponse?, CallResult) -> Void) throws -> Cobaltspeech_Cubic_CubicRecognizeCall {
    return try self.recognize(request, metadata: self.metadata, completion: completion)
  }

  /// Asynchronous. Bidirectional-streaming.
  func streamingRecognize(completion: ((CallResult) -> Void)?) throws -> Cobaltspeech_Cubic_CubicStreamingRecognizeCall {
    return try self.streamingRecognize(metadata: self.metadata, completion: completion)
  }

}

public class Cobaltspeech_Cubic_CubicServiceClient: ServiceClientBase, Cobaltspeech_Cubic_CubicService {
  /// Synchronous. Unary.
  internal func version(_ request: SwiftProtobuf.Google_Protobuf_Empty, metadata customMetadata: Metadata) throws -> Cobaltspeech_Cubic_VersionResponse {
    return try Cobaltspeech_Cubic_CubicVersionCallBase(channel)
      .run(request: request, metadata: customMetadata)
  }
  /// Asynchronous. Unary.
  @discardableResult
  internal func version(_ request: SwiftProtobuf.Google_Protobuf_Empty, metadata customMetadata: Metadata, completion: @escaping (Cobaltspeech_Cubic_VersionResponse?, CallResult) -> Void) throws -> Cobaltspeech_Cubic_CubicVersionCall {
    return try Cobaltspeech_Cubic_CubicVersionCallBase(channel)
      .start(request: request, metadata: customMetadata, completion: completion)
  }

  /// Synchronous. Unary.
  internal func listModels(_ request: Cobaltspeech_Cubic_ListModelsRequest, metadata customMetadata: Metadata) throws -> Cobaltspeech_Cubic_ListModelsResponse {
    return try Cobaltspeech_Cubic_CubicListModelsCallBase(channel)
      .run(request: request, metadata: customMetadata)
  }
  /// Asynchronous. Unary.
  @discardableResult
  internal func listModels(_ request: Cobaltspeech_Cubic_ListModelsRequest, metadata customMetadata: Metadata, completion: @escaping (Cobaltspeech_Cubic_ListModelsResponse?, CallResult) -> Void) throws -> Cobaltspeech_Cubic_CubicListModelsCall {
    return try Cobaltspeech_Cubic_CubicListModelsCallBase(channel)
      .start(request: request, metadata: customMetadata, completion: completion)
  }

  /// Synchronous. Unary.
  internal func recognize(_ request: Cobaltspeech_Cubic_RecognizeRequest, metadata customMetadata: Metadata) throws -> Cobaltspeech_Cubic_RecognitionResponse {
    return try Cobaltspeech_Cubic_CubicRecognizeCallBase(channel)
      .run(request: request, metadata: customMetadata)
  }
  /// Asynchronous. Unary.
  @discardableResult
  internal func recognize(_ request: Cobaltspeech_Cubic_RecognizeRequest, metadata customMetadata: Metadata, completion: @escaping (Cobaltspeech_Cubic_RecognitionResponse?, CallResult) -> Void) throws -> Cobaltspeech_Cubic_CubicRecognizeCall {
    return try Cobaltspeech_Cubic_CubicRecognizeCallBase(channel)
      .start(request: request, metadata: customMetadata, completion: completion)
  }

  /// Asynchronous. Bidirectional-streaming.
  /// Use methods on the returned object to stream messages,
  /// to wait for replies, and to close the connection.
  internal func streamingRecognize(metadata customMetadata: Metadata, completion: ((CallResult) -> Void)?) throws -> Cobaltspeech_Cubic_CubicStreamingRecognizeCall {
    return try Cobaltspeech_Cubic_CubicStreamingRecognizeCallBase(channel)
      .start(metadata: customMetadata, completion: completion)
  }

}

/// To build a server, implement a class that conforms to this protocol.
/// If one of the methods returning `ServerStatus?` returns nil,
/// it is expected that you have already returned a status to the client by means of `session.close`.
internal protocol Cobaltspeech_Cubic_CubicProvider: ServiceProvider {
  func version(request: SwiftProtobuf.Google_Protobuf_Empty, session: Cobaltspeech_Cubic_CubicVersionSession) throws -> Cobaltspeech_Cubic_VersionResponse
  func listModels(request: Cobaltspeech_Cubic_ListModelsRequest, session: Cobaltspeech_Cubic_CubicListModelsSession) throws -> Cobaltspeech_Cubic_ListModelsResponse
  func recognize(request: Cobaltspeech_Cubic_RecognizeRequest, session: Cobaltspeech_Cubic_CubicRecognizeSession) throws -> Cobaltspeech_Cubic_RecognitionResponse
  func streamingRecognize(session: Cobaltspeech_Cubic_CubicStreamingRecognizeSession) throws -> ServerStatus?
}

extension Cobaltspeech_Cubic_CubicProvider {
  internal var serviceName: String { return "cobaltspeech.cubic.Cubic" }

  /// Determines and calls the appropriate request handler, depending on the request's method.
  /// Throws `HandleMethodError.unknownMethod` for methods not handled by this service.
  internal func handleMethod(_ method: String, handler: Handler) throws -> ServerStatus? {
    switch method {
    case "/cobaltspeech.cubic.Cubic/Version":
      return try Cobaltspeech_Cubic_CubicVersionSessionBase(
        handler: handler,
        providerBlock: { try self.version(request: $0, session: $1 as! Cobaltspeech_Cubic_CubicVersionSessionBase) })
          .run()
    case "/cobaltspeech.cubic.Cubic/ListModels":
      return try Cobaltspeech_Cubic_CubicListModelsSessionBase(
        handler: handler,
        providerBlock: { try self.listModels(request: $0, session: $1 as! Cobaltspeech_Cubic_CubicListModelsSessionBase) })
          .run()
    case "/cobaltspeech.cubic.Cubic/Recognize":
      return try Cobaltspeech_Cubic_CubicRecognizeSessionBase(
        handler: handler,
        providerBlock: { try self.recognize(request: $0, session: $1 as! Cobaltspeech_Cubic_CubicRecognizeSessionBase) })
          .run()
    case "/cobaltspeech.cubic.Cubic/StreamingRecognize":
      return try Cobaltspeech_Cubic_CubicStreamingRecognizeSessionBase(
        handler: handler,
        providerBlock: { try self.streamingRecognize(session: $0 as! Cobaltspeech_Cubic_CubicStreamingRecognizeSessionBase) })
          .run()
    default:
      throw HandleMethodError.unknownMethod
    }
  }
}

internal protocol Cobaltspeech_Cubic_CubicVersionSession: ServerSessionUnary {}

fileprivate final class Cobaltspeech_Cubic_CubicVersionSessionBase: ServerSessionUnaryBase<SwiftProtobuf.Google_Protobuf_Empty, Cobaltspeech_Cubic_VersionResponse>, Cobaltspeech_Cubic_CubicVersionSession {}

internal protocol Cobaltspeech_Cubic_CubicListModelsSession: ServerSessionUnary {}

fileprivate final class Cobaltspeech_Cubic_CubicListModelsSessionBase: ServerSessionUnaryBase<Cobaltspeech_Cubic_ListModelsRequest, Cobaltspeech_Cubic_ListModelsResponse>, Cobaltspeech_Cubic_CubicListModelsSession {}

internal protocol Cobaltspeech_Cubic_CubicRecognizeSession: ServerSessionUnary {}

fileprivate final class Cobaltspeech_Cubic_CubicRecognizeSessionBase: ServerSessionUnaryBase<Cobaltspeech_Cubic_RecognizeRequest, Cobaltspeech_Cubic_RecognitionResponse>, Cobaltspeech_Cubic_CubicRecognizeSession {}

internal protocol Cobaltspeech_Cubic_CubicStreamingRecognizeSession: ServerSessionBidirectionalStreaming {
  /// Do not call this directly, call `receive()` in the protocol extension below instead.
  func _receive(timeout: DispatchTime) throws -> Cobaltspeech_Cubic_StreamingRecognizeRequest?
  /// Call this to wait for a result. Nonblocking.
  func receive(completion: @escaping (ResultOrRPCError<Cobaltspeech_Cubic_StreamingRecognizeRequest?>) -> Void) throws

  /// Send a message to the stream. Nonblocking.
  func send(_ message: Cobaltspeech_Cubic_RecognitionResponse, completion: @escaping (Error?) -> Void) throws
  /// Do not call this directly, call `send()` in the protocol extension below instead.
  func _send(_ message: Cobaltspeech_Cubic_RecognitionResponse, timeout: DispatchTime) throws

  /// Close the connection and send the status. Non-blocking.
  /// This method should be called if and only if your request handler returns a nil value instead of a server status;
  /// otherwise SwiftGRPC will take care of sending the status for you.
  func close(withStatus status: ServerStatus, completion: (() -> Void)?) throws
}

internal extension Cobaltspeech_Cubic_CubicStreamingRecognizeSession {
  /// Call this to wait for a result. Blocking.
  func receive(timeout: DispatchTime = .distantFuture) throws -> Cobaltspeech_Cubic_StreamingRecognizeRequest? { return try self._receive(timeout: timeout) }
}

internal extension Cobaltspeech_Cubic_CubicStreamingRecognizeSession {
  /// Send a message to the stream and wait for the send operation to finish. Blocking.
  func send(_ message: Cobaltspeech_Cubic_RecognitionResponse, timeout: DispatchTime = .distantFuture) throws { try self._send(message, timeout: timeout) }
}

fileprivate final class Cobaltspeech_Cubic_CubicStreamingRecognizeSessionBase: ServerSessionBidirectionalStreamingBase<Cobaltspeech_Cubic_StreamingRecognizeRequest, Cobaltspeech_Cubic_RecognitionResponse>, Cobaltspeech_Cubic_CubicStreamingRecognizeSession {}

