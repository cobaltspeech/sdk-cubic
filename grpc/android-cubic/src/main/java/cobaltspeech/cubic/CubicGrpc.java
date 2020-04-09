package cobaltspeech.cubic;

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
    value = "by gRPC proto compiler (version 1.24.0)",
    comments = "Source: cubic.proto")
public final class CubicGrpc {

  private CubicGrpc() {}

  public static final String SERVICE_NAME = "cobaltspeech.cubic.Cubic";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      cobaltspeech.cubic.CubicOuterClass.VersionResponse> getVersionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Version",
      requestType = com.google.protobuf.Empty.class,
      responseType = cobaltspeech.cubic.CubicOuterClass.VersionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.google.protobuf.Empty,
      cobaltspeech.cubic.CubicOuterClass.VersionResponse> getVersionMethod() {
    io.grpc.MethodDescriptor<com.google.protobuf.Empty, cobaltspeech.cubic.CubicOuterClass.VersionResponse> getVersionMethod;
    if ((getVersionMethod = CubicGrpc.getVersionMethod) == null) {
      synchronized (CubicGrpc.class) {
        if ((getVersionMethod = CubicGrpc.getVersionMethod) == null) {
          CubicGrpc.getVersionMethod = getVersionMethod =
              io.grpc.MethodDescriptor.<com.google.protobuf.Empty, cobaltspeech.cubic.CubicOuterClass.VersionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Version"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  com.google.protobuf.Empty.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  cobaltspeech.cubic.CubicOuterClass.VersionResponse.getDefaultInstance()))
              .build();
        }
      }
    }
    return getVersionMethod;
  }

  private static volatile io.grpc.MethodDescriptor<cobaltspeech.cubic.CubicOuterClass.ListModelsRequest,
      cobaltspeech.cubic.CubicOuterClass.ListModelsResponse> getListModelsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ListModels",
      requestType = cobaltspeech.cubic.CubicOuterClass.ListModelsRequest.class,
      responseType = cobaltspeech.cubic.CubicOuterClass.ListModelsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<cobaltspeech.cubic.CubicOuterClass.ListModelsRequest,
      cobaltspeech.cubic.CubicOuterClass.ListModelsResponse> getListModelsMethod() {
    io.grpc.MethodDescriptor<cobaltspeech.cubic.CubicOuterClass.ListModelsRequest, cobaltspeech.cubic.CubicOuterClass.ListModelsResponse> getListModelsMethod;
    if ((getListModelsMethod = CubicGrpc.getListModelsMethod) == null) {
      synchronized (CubicGrpc.class) {
        if ((getListModelsMethod = CubicGrpc.getListModelsMethod) == null) {
          CubicGrpc.getListModelsMethod = getListModelsMethod =
              io.grpc.MethodDescriptor.<cobaltspeech.cubic.CubicOuterClass.ListModelsRequest, cobaltspeech.cubic.CubicOuterClass.ListModelsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ListModels"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  cobaltspeech.cubic.CubicOuterClass.ListModelsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  cobaltspeech.cubic.CubicOuterClass.ListModelsResponse.getDefaultInstance()))
              .build();
        }
      }
    }
    return getListModelsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<cobaltspeech.cubic.CubicOuterClass.RecognizeRequest,
      cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> getRecognizeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Recognize",
      requestType = cobaltspeech.cubic.CubicOuterClass.RecognizeRequest.class,
      responseType = cobaltspeech.cubic.CubicOuterClass.RecognitionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<cobaltspeech.cubic.CubicOuterClass.RecognizeRequest,
      cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> getRecognizeMethod() {
    io.grpc.MethodDescriptor<cobaltspeech.cubic.CubicOuterClass.RecognizeRequest, cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> getRecognizeMethod;
    if ((getRecognizeMethod = CubicGrpc.getRecognizeMethod) == null) {
      synchronized (CubicGrpc.class) {
        if ((getRecognizeMethod = CubicGrpc.getRecognizeMethod) == null) {
          CubicGrpc.getRecognizeMethod = getRecognizeMethod =
              io.grpc.MethodDescriptor.<cobaltspeech.cubic.CubicOuterClass.RecognizeRequest, cobaltspeech.cubic.CubicOuterClass.RecognitionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Recognize"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  cobaltspeech.cubic.CubicOuterClass.RecognizeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  cobaltspeech.cubic.CubicOuterClass.RecognitionResponse.getDefaultInstance()))
              .build();
        }
      }
    }
    return getRecognizeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest,
      cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> getStreamingRecognizeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamingRecognize",
      requestType = cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest.class,
      responseType = cobaltspeech.cubic.CubicOuterClass.RecognitionResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest,
      cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> getStreamingRecognizeMethod() {
    io.grpc.MethodDescriptor<cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest, cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> getStreamingRecognizeMethod;
    if ((getStreamingRecognizeMethod = CubicGrpc.getStreamingRecognizeMethod) == null) {
      synchronized (CubicGrpc.class) {
        if ((getStreamingRecognizeMethod = CubicGrpc.getStreamingRecognizeMethod) == null) {
          CubicGrpc.getStreamingRecognizeMethod = getStreamingRecognizeMethod =
              io.grpc.MethodDescriptor.<cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest, cobaltspeech.cubic.CubicOuterClass.RecognitionResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamingRecognize"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  cobaltspeech.cubic.CubicOuterClass.RecognitionResponse.getDefaultInstance()))
              .build();
        }
      }
    }
    return getStreamingRecognizeMethod;
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
        io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.VersionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getVersionMethod(), responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of available speech recognition models
     * </pre>
     */
    public void listModels(cobaltspeech.cubic.CubicOuterClass.ListModelsRequest request,
        io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.ListModelsResponse> responseObserver) {
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
    public void recognize(cobaltspeech.cubic.CubicOuterClass.RecognizeRequest request,
        io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRecognizeMethod(), responseObserver);
    }

    /**
     * <pre>
     * Performs bidirectional streaming speech recognition.  Receive results while
     * sending audio.  This method is only available via GRPC and not via
     * HTTP+JSON. However, a web browser may use websockets to use this service.
     * </pre>
     */
    public io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest> streamingRecognize(
        io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> responseObserver) {
      return asyncUnimplementedStreamingCall(getStreamingRecognizeMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getVersionMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.google.protobuf.Empty,
                cobaltspeech.cubic.CubicOuterClass.VersionResponse>(
                  this, METHODID_VERSION)))
          .addMethod(
            getListModelsMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                cobaltspeech.cubic.CubicOuterClass.ListModelsRequest,
                cobaltspeech.cubic.CubicOuterClass.ListModelsResponse>(
                  this, METHODID_LIST_MODELS)))
          .addMethod(
            getRecognizeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                cobaltspeech.cubic.CubicOuterClass.RecognizeRequest,
                cobaltspeech.cubic.CubicOuterClass.RecognitionResponse>(
                  this, METHODID_RECOGNIZE)))
          .addMethod(
            getStreamingRecognizeMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest,
                cobaltspeech.cubic.CubicOuterClass.RecognitionResponse>(
                  this, METHODID_STREAMING_RECOGNIZE)))
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
        io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.VersionResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getVersionMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Retrieves a list of available speech recognition models
     * </pre>
     */
    public void listModels(cobaltspeech.cubic.CubicOuterClass.ListModelsRequest request,
        io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.ListModelsResponse> responseObserver) {
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
    public void recognize(cobaltspeech.cubic.CubicOuterClass.RecognizeRequest request,
        io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> responseObserver) {
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
    public io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest> streamingRecognize(
        io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getStreamingRecognizeMethod(), getCallOptions()), responseObserver);
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
    public cobaltspeech.cubic.CubicOuterClass.VersionResponse version(com.google.protobuf.Empty request) {
      return blockingUnaryCall(
          getChannel(), getVersionMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Retrieves a list of available speech recognition models
     * </pre>
     */
    public cobaltspeech.cubic.CubicOuterClass.ListModelsResponse listModels(cobaltspeech.cubic.CubicOuterClass.ListModelsRequest request) {
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
    public cobaltspeech.cubic.CubicOuterClass.RecognitionResponse recognize(cobaltspeech.cubic.CubicOuterClass.RecognizeRequest request) {
      return blockingUnaryCall(
          getChannel(), getRecognizeMethod(), getCallOptions(), request);
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
    public com.google.common.util.concurrent.ListenableFuture<cobaltspeech.cubic.CubicOuterClass.VersionResponse> version(
        com.google.protobuf.Empty request) {
      return futureUnaryCall(
          getChannel().newCall(getVersionMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Retrieves a list of available speech recognition models
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<cobaltspeech.cubic.CubicOuterClass.ListModelsResponse> listModels(
        cobaltspeech.cubic.CubicOuterClass.ListModelsRequest request) {
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
    public com.google.common.util.concurrent.ListenableFuture<cobaltspeech.cubic.CubicOuterClass.RecognitionResponse> recognize(
        cobaltspeech.cubic.CubicOuterClass.RecognizeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRecognizeMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_VERSION = 0;
  private static final int METHODID_LIST_MODELS = 1;
  private static final int METHODID_RECOGNIZE = 2;
  private static final int METHODID_STREAMING_RECOGNIZE = 3;

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
              (io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.VersionResponse>) responseObserver);
          break;
        case METHODID_LIST_MODELS:
          serviceImpl.listModels((cobaltspeech.cubic.CubicOuterClass.ListModelsRequest) request,
              (io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.ListModelsResponse>) responseObserver);
          break;
        case METHODID_RECOGNIZE:
          serviceImpl.recognize((cobaltspeech.cubic.CubicOuterClass.RecognizeRequest) request,
              (io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.RecognitionResponse>) responseObserver);
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
              (io.grpc.stub.StreamObserver<cobaltspeech.cubic.CubicOuterClass.RecognitionResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
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
              .addMethod(getVersionMethod())
              .addMethod(getListModelsMethod())
              .addMethod(getRecognizeMethod())
              .addMethod(getStreamingRecognizeMethod())
              .build();
        }
      }
    }
    return result;
  }
}
