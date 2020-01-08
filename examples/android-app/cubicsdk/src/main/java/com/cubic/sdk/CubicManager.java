package com.cubic.sdk;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.cubic.sdk.exception.AudioPermissionException;
import com.cubic.sdk.exception.ChannelShutdownException;
import com.cubic.sdk.exception.NetworkException;
import com.cubic.sdk.model.ConnectionConfiguration;
import com.google.protobuf.ByteString;

import com.cubic.sdk.audio.AudioRecordController;
import com.cubic.sdk.audio.IAudioRecordController;
import com.cubic.sdk.audio.OnAudioRecordDataChangeListener;
import com.cubic.sdk.audio.model.AudioRecordConfig;
import com.cubic.sdk.common.Logger;
import com.cubic.sdk.model.CubicAudioConfiguration;

import java.util.ArrayList;
import java.util.List;

import cobaltspeech.cubic.CubicGrpc;
import cobaltspeech.cubic.CubicOuterClass;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public final class CubicManager implements ICubicManager {

    private final Context mContext;
    private final Lifecycle mLifecycle;

    private final Handler mMainThreadHandler;
    private final ConnectionConfiguration mConnectionConfiguration;
    private final OnCubicChangeListener mOnCubicChangeListener;

    private ManagedChannel mCubicChannel;
    private CubicGrpc.CubicStub mCubicService;

    private final List<CubicAudioConfiguration> mCubicAudioConfigurations = new ArrayList<>();
    private CubicAudioConfiguration mCubicAudioConfiguration;

    private final IAudioRecordController mIAudioRecordController;

    private CubicManager(@NonNull Context context,
                         @NonNull ConnectionConfiguration configuration,
                         Lifecycle lifecycle,
                         @NonNull OnCubicChangeListener listener) {
        mContext = context;
        mMainThreadHandler = new Handler(context.getMainLooper());
        mConnectionConfiguration = configuration;
        mLifecycle = lifecycle;
        mOnCubicChangeListener = listener;
        mIAudioRecordController = new AudioRecordController(new OnAudioRecordDataChangeListener() {
            @Override
            public void onDataChange(@NonNull byte[] data) {
                ByteString audio = ByteString.copyFrom(data);
                CubicOuterClass.RecognitionAudio recognitionAudio = CubicOuterClass.RecognitionAudio.newBuilder()
                        .setData(audio)
                        .build();
                CubicOuterClass.StreamingRecognizeRequest msg = CubicOuterClass.StreamingRecognizeRequest.newBuilder()
                        .setAudio(recognitionAudio)
                        .build();
                mRecognitionRequestObserver.onNext(msg);
            }

            @Override
            public void onError(Exception e) {
                Logger.exception(e);
                mMainThreadHandler.post(() -> mOnCubicChangeListener.onError(e));
            }
        });
        initAutoConnection();
    }

    public CubicManager(@NonNull Context context,
                        Lifecycle lifecycle,
                        @NonNull OnCubicChangeListener listener) {
        this(context,
                new ConnectionConfiguration(
                        BuildConfig.HOST,
                        BuildConfig.SECURE_CONNECTION
                ),
                lifecycle,
                listener
        );
    }

    public CubicManager(@NonNull Context context,
                        @NonNull OnCubicChangeListener listener) {
        this(context,
                new ConnectionConfiguration(
                        BuildConfig.HOST,
                        BuildConfig.SECURE_CONNECTION
                ),
                null,
                listener
        );
    }

    private void initAutoConnection() {
        if (mLifecycle == null) {
            return;
        }
        mLifecycle.addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                switch (event) {
                    case ON_RESUME: {
                        connect();
                        break;
                    }
                    case ON_STOP: {
                        stopTalk();
                        disconnect();
                        break;
                    }
                    case ON_DESTROY: {
                        mLifecycle.removeObserver(this);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void connect() {
        int stateP = ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO);
        if (stateP != PackageManager.PERMISSION_GRANTED) {
            mMainThreadHandler.post(() -> {
                mOnCubicChangeListener.onError(new AudioPermissionException());
                mOnCubicChangeListener.onDisconnect();
            });
            return;
        }

        if (mCubicService == null) {
            mMainThreadHandler.post(mOnCubicChangeListener::onConnecting);

            ManagedChannelBuilder<?> builder = ManagedChannelBuilder
                    .forTarget(mConnectionConfiguration.getHost());

            if (!mConnectionConfiguration.isSecure()) {
                builder.usePlaintext();
            }
            mCubicChannel = builder.build();
            mCubicService = CubicGrpc.newStub(mCubicChannel);

            CubicOuterClass.ListModelsRequest listModelsRequest = CubicOuterClass.ListModelsRequest.newBuilder().build();
            mCubicService.listModels(listModelsRequest, new StreamObserver<CubicOuterClass.ListModelsResponse>() {
                @Override
                public void onNext(CubicOuterClass.ListModelsResponse value) {
                    mMainThreadHandler.post(() -> {
                        List<CubicOuterClass.Model> list = value.getModelsList();
                        mCubicAudioConfigurations.clear();
                        for (CubicOuterClass.Model model : list) {
                            mCubicAudioConfigurations.add(new CubicAudioConfiguration(model));
                        }
                        mOnCubicChangeListener.onGetAudioConfigurations(mCubicAudioConfigurations);

                        if (!mCubicAudioConfigurations.isEmpty())
                            setAudioConfiguration(mCubicAudioConfigurations.get(0));
                    });
                }

                @Override
                public void onError(Throwable t) {
                    String message = t.getMessage();
                    Exception e = new Exception(message);
                    Logger.exception(e);
                    mMainThreadHandler.post(() -> {
                        if (t instanceof StatusRuntimeException) {
                            if (TextUtils.equals("UNAVAILABLE", message)
                                    || message.contains("Failed trying to connect with proxy")) {
                                mOnCubicChangeListener.onError(new NetworkException());
                            } else if (message.contains("Channel shutdownNow invoked")) {
                                mOnCubicChangeListener.onError(new ChannelShutdownException());
                            }
                        } else {
                            mOnCubicChangeListener.onError(e);
                        }
                        mOnCubicChangeListener.onDisconnect();
                    });
                }

                @Override
                public void onCompleted() {
                    mMainThreadHandler.post(mOnCubicChangeListener::onConnect);
                }
            });
        }
    }

    @Override
    public void disconnect() {
        if (mCubicChannel != null && !mCubicChannel.isShutdown()) {
            mCubicChannel.shutdownNow();
            mCubicChannel = null;
        }
        mCubicService = null;

        mCubicAudioConfigurations.clear();
        mOnCubicChangeListener.onDisconnect();
    }

    @Override
    public void setAudioConfiguration(CubicAudioConfiguration configuration) {
        mCubicAudioConfiguration = configuration;
    }

    @Override
    public List<CubicAudioConfiguration> getAudioConfigurations() {
        return mCubicAudioConfigurations;
    }

    private Runnable mRecognitionRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCubicAudioConfiguration == null) {
                mMainThreadHandler.post(() -> {
                    Exception e = new Exception("Transcription service, no audio configuration");
                    mOnCubicChangeListener.onError(e);
                });
                return;
            }

            mRecognitionRequestObserver = mCubicService.streamingRecognize(mRecognitionResponseObserver);

            CubicOuterClass.StreamingRecognizeRequest configs = CubicOuterClass.StreamingRecognizeRequest.newBuilder()
                    .setConfig(CubicOuterClass.RecognitionConfig.newBuilder()
                            .setModelId(mCubicAudioConfiguration.getConfiguration().getId())
                            .setAudioEncoding(CubicOuterClass.RecognitionConfig.Encoding.RAW_LINEAR16)
                            .setEnableWordTimeOffsets(false)
                            .setEnableRawTranscript(true)
                            .setEnableConfusionNetwork(false)
                            .setEnableWordConfidence(false)
                            .build())
                    .build();
            mRecognitionRequestObserver.onNext(configs);

            CubicOuterClass.Model configuration = mCubicAudioConfiguration.getConfiguration();
            CubicOuterClass.ModelAttributes attributes = configuration.getAttributes();
            int rate = attributes.getSampleRate();
            mIAudioRecordController.startRecord(new AudioRecordConfig(rate));
            mRecognitionRequestObserver.onCompleted();
        }
    };

    private StreamObserver<CubicOuterClass.StreamingRecognizeRequest> mRecognitionRequestObserver;

    private StreamObserver<CubicOuterClass.RecognitionResponse> mRecognitionResponseObserver = new StreamObserver<CubicOuterClass.RecognitionResponse>() {
        @Override
        public void onNext(CubicOuterClass.RecognitionResponse value) {
            StringBuilder result = new StringBuilder();
            for (CubicOuterClass.RecognitionResult r : value.getResultsList()) {
                if (!r.getIsPartial() && r.getAlternativesCount() > 0) {
                    result.append(r.getAlternatives(0).getTranscript());
                }
            }
            if (TextUtils.isEmpty(result.toString())) {
                return;
            }
            mMainThreadHandler.post(() -> mOnCubicChangeListener.onText(result.toString()));
        }

        @Override
        public void onError(Throwable t) {
            Exception e = new Exception(t.getMessage());
            Logger.exception(e);
            mMainThreadHandler.post(() -> mOnCubicChangeListener.onError(e));
        }

        @Override
        public void onCompleted() { }
    };

    @Override
    public void talk() {
        HandlerThread audioRecordThread = new HandlerThread("AudioRecordThread");
        audioRecordThread.start();
        Handler recognitionHandler = new Handler(audioRecordThread.getLooper());
        recognitionHandler.post(mRecognitionRunnable);
    }

    @Override
    public void stopTalk() {
        mIAudioRecordController.stopRecord();
    }
}
