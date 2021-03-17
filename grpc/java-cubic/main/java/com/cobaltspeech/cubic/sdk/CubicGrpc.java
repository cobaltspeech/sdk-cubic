package com.cobaltspeech.cubic.sdk;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * Service that implements the Cobalt Cubic Speech Recognition API
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.21.0)",
    comments = "Source: cubic.proto")
public final class CubicGrpc {

  private CubicGrpc() {}

  public static final String SERVICE_NAME = "cobaltspeech.cubic.Cubic";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse> getVersionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Version",
      requestType = com.google.protobuf.Empty.class,
      responseType = com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse> getVersionMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse> getVersionMethod;
    if ((getVersionMethod = CubicGrpc.getVersionMethod) == null) {
      synchronized (CubicGrpc.class) {
        if ((getVersionMethod = CubicGrpc.getVersionMethod) == null) {
          CubicGrpc.getVersionMethod = getVersionMethod = 
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "cobaltspeech.cubic.Cubic", "Version"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CubicMethodDescriptorSupplier("Version"))
                  .build();
          }
        }
     }
     return getVersionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse> getListModelsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListModels",
      requestType = com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest.class,
      responseType = com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse> getListModelsMethod() {
    io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest, com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse> getListModelsMethod;
    if ((getListModelsMethod = CubicGrpc.getListModelsMethod) == null) {
      synchronized (CubicGrpc.class) {
        if ((getListModelsMethod = CubicGrpc.getListModelsMethod) == null) {
          CubicGrpc.getListModelsMethod = getListModelsMethod = 
              io.grpc.MethodDescriptor.<com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest, com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "cobaltspeech.cubic.Cubic", "ListModels"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CubicMethodDescriptorSupplier("ListModels"))
                  .build();
          }
        }
     }
     return getListModelsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> getRecognizeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Recognize",
      requestType = com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest.class,
      responseType = com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> getRecognizeMethod() {
    io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest, com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> getRecognizeMethod;
    if ((getRecognizeMethod = CubicGrpc.getRecognizeMethod) == null) {
      synchronized (CubicGrpc.class) {
        if ((getRecognizeMethod = CubicGrpc.getRecognizeMethod) == null) {
          CubicGrpc.getRecognizeMethod = getRecognizeMethod = 
              io.grpc.MethodDescriptor.<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest, com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "cobaltspeech.cubic.Cubic", "Recognize"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CubicMethodDescriptorSupplier("Recognize"))
                  .build();
          }
        }
     }
     return getRecognizeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.StreamingRecognizeRequest,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> getStreamingRecognizeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamingRecognize",
      requestType = com.cobaltspeech.cubic.sdk.CubicOuterClass.StreamingRecognizeRequest.class,
      responseType = com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.StreamingRecognizeRequest,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> getStreamingRecognizeMethod() {
    io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.StreamingRecognizeRequest, com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> getStreamingRecognizeMethod;
    if ((getStreamingRecognizeMethod = CubicGrpc.getStreamingRecognizeMethod) == null) {
      synchronized (CubicGrpc.class) {
        if ((getStreamingRecognizeMethod = CubicGrpc.getStreamingRecognizeMethod) == null) {
          CubicGrpc.getStreamingRecognizeMethod = getStreamingRecognizeMethod = 
              io.grpc.MethodDescriptor.<com.cobaltspeech.cubic.sdk.CubicOuterClass.StreamingRecognizeRequest, com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "cobaltspeech.cubic.Cubic", "StreamingRecognize"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cobaltspeech.cubic.sdk.CubicOuterClass.StreamingRecognizeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CubicMethodDescriptorSupplier("StreamingRecognize"))
                  .build();
          }
        }
     }
     return getStreamingRecognizeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse> getCompileContextMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CompileContext",
      requestType = com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest.class,
      responseType = com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest,
      com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse> getCompileContextMethod() {
    io.grpc.MethodDescriptor<com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest, com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse> getCompileContextMethod;
    if ((getCompileContextMethod = CubicGrpc.getCompileContextMethod) == null) {
      synchronized (CubicGrpc.class) {
        if ((getCompileContextMethod = CubicGrpc.getCompileContextMethod) == null) {
          CubicGrpc.getCompileContextMethod = getCompileContextMethod = 
              io.grpc.MethodDescriptor.<com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest, com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "cobaltspeech.cubic.Cubic", "CompileContext"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new CubicMethodDescriptorSupplier("CompileContext"))
                  .build();
          }
        }
     }
     return getCompileContextMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CubicStub newStub(io.grpc.Channel channel) {
    return new CubicStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CubicBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CubicBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CubicFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CubicFutureStub(channel);
  }

  /**
   * <pre>
   * Service that implements the Cobalt Cubic Speech Recognition API
   * </pre>
   */
  public static abstract class CubicImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Queries the Version of the Server
     * </pre>
     */
    public void version(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getVersionMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of available speech recognition models
     * </pre>
     */
    public void listModels(com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest request,
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getListModelsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Performs synchronous speech recognition: receive results after all audio
     * has been sent and processed.  It is expected that this request be typically
     * used for short audio content: less than a minute long.  For longer content,
     * the `StreamingRecognize` method should be preferred.
     * </pre>
     */
    public void recognize(com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest request,
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRecognizeMethod(), responseObserver);
    }

    /**
     * <pre>
     * Performs bidirectional streaming speech recognition.  Receive results while
     * sending audio.  This method is only available via GRPC and not via
     * HTTP+JSON. However, a web browser may use websockets to use this service.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.StreamingRecognizeRequest> streamingRecognize(
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getStreamingRecognizeMethod(), responseObserver);
    }

    /**
     * <pre>
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
     * </pre>
     */
    public void compileContext(com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest request,
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCompileContextMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getVersionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse>(
                  this, METHODID_VERSION)))
          .addMethod(
            getListModelsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest,
                com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse>(
                  this, METHODID_LIST_MODELS)))
          .addMethod(
            getRecognizeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest,
                com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse>(
                  this, METHODID_RECOGNIZE)))
          .addMethod(
            getStreamingRecognizeMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                com.cobaltspeech.cubic.sdk.CubicOuterClass.StreamingRecognizeRequest,
                com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse>(
                  this, METHODID_STREAMING_RECOGNIZE)))
          .addMethod(
            getCompileContextMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest,
                com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse>(
                  this, METHODID_COMPILE_CONTEXT)))
          .build();
    }
  }

  /**
   * <pre>
   * Service that implements the Cobalt Cubic Speech Recognition API
   * </pre>
   */
  public static final class CubicStub extends io.grpc.stub.AbstractStub<CubicStub> {
    private CubicStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CubicStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CubicStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CubicStub(channel, callOptions);
    }

    /**
     * <pre>
     * Queries the Version of the Server
     * </pre>
     */
    public void version(com.google.protobuf.Empty request,
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getVersionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of available speech recognition models
     * </pre>
     */
    public void listModels(com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest request,
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getListModelsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Performs synchronous speech recognition: receive results after all audio
     * has been sent and processed.  It is expected that this request be typically
     * used for short audio content: less than a minute long.  For longer content,
     * the `StreamingRecognize` method should be preferred.
     * </pre>
     */
    public void recognize(com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest request,
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRecognizeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Performs bidirectional streaming speech recognition.  Receive results while
     * sending audio.  This method is only available via GRPC and not via
     * HTTP+JSON. However, a web browser may use websockets to use this service.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.StreamingRecognizeRequest> streamingRecognize(
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getStreamingRecognizeMethod(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
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
     * </pre>
     */
    public void compileContext(com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest request,
        io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCompileContextMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * Service that implements the Cobalt Cubic Speech Recognition API
   * </pre>
   */
  public static final class CubicBlockingStub extends io.grpc.stub.AbstractStub<CubicBlockingStub> {
    private CubicBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CubicBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CubicBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CubicBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Queries the Version of the Server
     * </pre>
     */
    public com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse version(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), getVersionMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves a list of available speech recognition models
     * </pre>
     */
    public com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse listModels(com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest request) {
      return blockingUnaryCall(
          getChannel(), getListModelsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Performs synchronous speech recognition: receive results after all audio
     * has been sent and processed.  It is expected that this request be typically
     * used for short audio content: less than a minute long.  For longer content,
     * the `StreamingRecognize` method should be preferred.
     * </pre>
     */
    public com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse recognize(com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest request) {
      return blockingUnaryCall(
          getChannel(), getRecognizeMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
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
     * </pre>
     */
    public com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse compileContext(com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest request) {
      return blockingUnaryCall(
          getChannel(), getCompileContextMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Service that implements the Cobalt Cubic Speech Recognition API
   * </pre>
   */
  public static final class CubicFutureStub extends io.grpc.stub.AbstractStub<CubicFutureStub> {
    private CubicFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CubicFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CubicFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CubicFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Queries the Version of the Server
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse> version(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getVersionMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves a list of available speech recognition models
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse> listModels(
        com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getListModelsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Performs synchronous speech recognition: receive results after all audio
     * has been sent and processed.  It is expected that this request be typically
     * used for short audio content: less than a minute long.  For longer content,
     * the `StreamingRecognize` method should be preferred.
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse> recognize(
        com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRecognizeMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
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
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse> compileContext(
        com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCompileContextMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VERSION = 0;
  private static final int METHODID_LIST_MODELS = 1;
  private static final int METHODID_RECOGNIZE = 2;
  private static final int METHODID_COMPILE_CONTEXT = 3;
  private static final int METHODID_STREAMING_RECOGNIZE = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CubicImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CubicImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_VERSION:
          serviceImpl.version((com.google.protobuf.Empty) request,
              (io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.VersionResponse>) responseObserver);
          break;
        case METHODID_LIST_MODELS:
          serviceImpl.listModels((com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsRequest) request,
              (io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.ListModelsResponse>) responseObserver);
          break;
        case METHODID_RECOGNIZE:
          serviceImpl.recognize((com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognizeRequest) request,
              (io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse>) responseObserver);
          break;
        case METHODID_COMPILE_CONTEXT:
          serviceImpl.compileContext((com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextRequest) request,
              (io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.CompileContextResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STREAMING_RECOGNIZE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.streamingRecognize(
              (io.grpc.stub.StreamObserver<com.cobaltspeech.cubic.sdk.CubicOuterClass.RecognitionResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class CubicBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CubicBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.cobaltspeech.cubic.sdk.CubicOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Cubic");
    }
  }

  private static final class CubicFileDescriptorSupplier
      extends CubicBaseDescriptorSupplier {
    CubicFileDescriptorSupplier() {}
  }

  private static final class CubicMethodDescriptorSupplier
      extends CubicBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CubicMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CubicGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CubicFileDescriptorSupplier())
              .addMethod(getVersionMethod())
              .addMethod(getListModelsMethod())
              .addMethod(getRecognizeMethod())
              .addMethod(getStreamingRecognizeMethod())
              .addMethod(getCompileContextMethod())
              .build();
        }
      }
    }
    return result;
  }
}
