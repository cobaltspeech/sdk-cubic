// Copyright (2019) Cobalt Speech and Language, Inc. All rights reserved.

package com.cobaltspeech.cubicdemo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cobaltspeech.cubic.CubicGrpc;
import com.cobaltspeech.cubic.CubicOuterClass;
import com.cobaltspeech.cubic.CubicOuterClass.ListModelsRequest;
import com.cobaltspeech.cubic.CubicOuterClass.ListModelsResponse;
import com.cobaltspeech.cubic.CubicOuterClass.Model;
import com.cobaltspeech.cubic.CubicOuterClass.RecognitionAudio;
import com.cobaltspeech.cubic.CubicOuterClass.RecognitionConfig;
import com.cobaltspeech.cubic.CubicOuterClass.RecognitionResponse;
import com.cobaltspeech.cubic.CubicOuterClass.StreamingRecognizeRequest;
import com.cobaltspeech.cubic.CubicOuterClass.VersionResponse;
import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.Locale;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int SAMPLE_BLOCK_SIZE = 8192;

    StringBuilder mTranscriptionText; // Holder for text transcripts to be displayed

    private EditText editText_URL;
    private Switch switchSecureConnection;
    private Button buttonConnect;
    private Spinner spinnerModels;
    private Button buttonPushToTalk;
    private TextView textViewResults;
    private TextView labelVersions;
    private AudioRecorderWrapper mAudioRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
   }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the audio thread (if it's running).
        mAudioRecorder.stopRunning();
    }

    @SuppressLint("ClickableViewAccessibility") // Allow onTouchListener without errors
    private void setupViews() {
        // Find UI Views
        editText_URL = this.findViewById(R.id.editText_URL);
        switchSecureConnection = this.findViewById(R.id.switchSecureConnection);
        buttonConnect = this.findViewById(R.id.buttonConnect);
        spinnerModels = this.findViewById(R.id.spinnerModels);
        buttonPushToTalk = this.findViewById(R.id.buttonPushToTalk);
        textViewResults = this.findViewById(R.id.textViewResults);
        labelVersions = this.findViewById(R.id.labelVersions);

        // Initialize correct states
        mTranscriptionText = new StringBuilder();
        textViewResults.setText(getString(R.string.transcriptions_will_appear_here));
        editText_URL.setText(getString(R.string.demo_server_url));
        switchSecureConnection.setChecked(true);
        spinnerModels.setEnabled(false);
        buttonPushToTalk.setEnabled(false);

        // Set onClick listeners
        buttonConnect.setOnClickListener(v -> connect());
        buttonPushToTalk.setOnTouchListener((v, event) -> {
            // Here we use an onTouchListener over an onClickListener to allow for Push-To-Talk functionality.
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Update the UI
                    buttonPushToTalk.setPressed(true);

                    // Reset the transcription results
                    mTranscriptionText.setLength(0);
                    textViewResults.setText(getString(R.string.transcriptions_will_appear_here));

                    // Start recording/streaming thread
                    mRecognitionThread = new HandlerThread("recognitionThread");
                    mRecognitionThread.start();
                    mRecognitionHandler = new Handler(mRecognitionThread.getLooper());
                    mRecognitionHandler.post(mRecognitionRunnable);
                    break;
                case MotionEvent.ACTION_UP:
                    // Update the UI
                    buttonPushToTalk.setPressed(false);

                    // Stop recording/streaming thread
                    mAudioRecorder.stopRunning();
                    break;
            }
            return true;
        });
        spinnerModels.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedModel = mAvailableModels.get(position);
                Log.i(TAG, "Selected model:" + mSelectedModel.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedModel = null;
            }
        });
    }


    // gRPC client and stream observers.
    private ManagedChannel mCubicChannel; // This is the main connection.
    private CubicGrpc.CubicStub mCubicService; // This is the main connection.

    private ArrayList<CubicOuterClass.Model> mAvailableModels;
    private CubicOuterClass.Model mSelectedModel;

    private void connect() {
        if (mCubicService == null) {
            // Setup gRPC stuff
            ManagedChannelBuilder<?> builder = ManagedChannelBuilder
                    .forTarget(editText_URL.getText().toString());
            Log.v(TAG, "Connecting to url: " + editText_URL.getText().toString());
            if (!switchSecureConnection.isChecked()) {
                Log.v(TAG, "Using plain text in connection.");
                builder.usePlaintext();
            }
            mCubicChannel = builder.build();
            mCubicService = CubicGrpc.newStub(mCubicChannel);

            // Fetch the Models
            ListModelsRequest listModelsRequest = ListModelsRequest.newBuilder().build();
            mCubicService.listModels(listModelsRequest, new StreamObserver<ListModelsResponse>() {
                @Override
                public void onNext(ListModelsResponse value) {
                    Log.i(TAG, "ModelList Response:" + value.toString());

                    // Update the UI (On the UI Thread!  Throws cryptic errors if not.)
                    runOnUiThread(() -> {
                        // Stash the models
                        mAvailableModels = new ArrayList<>(value.getModelsList());

                        // Update the model spinner for users to pick which model to use.
                        ArrayList<String> modelNames = new ArrayList<>();
                        for (Model m : mAvailableModels) {
                            modelNames.add(m.getName());
                        }
                        spinnerModels.setEnabled(true);
                        spinnerModels.setAdapter(new ArrayAdapter<>(
                                MainActivity.this,
                                android.R.layout.simple_list_item_1,
                                modelNames));
                    });
                }

                @Override
                public void onError(Throwable t) {
                    Log.e(TAG, "Error fetching models.", t);
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "Error fetching models: " + t.toString(), Toast.LENGTH_SHORT).show();
                        updateConnectUI(false); // We'll assume that the connection failed, and they need to try again.
                    });
                }

                @Override
                public void onCompleted() {
                    // Once we have the models, we can consider it safe to start transcribing stuff,
                    // so we enable the UI (again, on the main UI thread).
                    // Note: Even though we are also fetching the version, we aren't concerned with
                    // it finishing successfully.
                    runOnUiThread(() -> updateConnectUI(true));
                }
            });

            // Fetch the Version (for information only, so not critical to wait for result)
            CubicOuterClass.VersionRequest versionRequest = CubicOuterClass.VersionRequest.newBuilder().build();
            mCubicService.version(versionRequest, new StreamObserver<VersionResponse>() {
                @Override
                public void onNext(VersionResponse value) {
                    Log.i(TAG, "Version response: " + value.toString());

                    // Update the UI (On the UI Thread!  Throws cryptic errors if not.)
                    runOnUiThread(() -> MainActivity.this.labelVersions.setText(
                            String.format(Locale.US,
                                    "Versions: CubicSvr %s, Cubic %s",
                                    value.getServer(), value.getCubic())));
                }

                @Override
                public void onError(Throwable t) {
                    Log.e(TAG, "Error fetching server versions", t);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error fetching server version: " + t.toString(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCompleted() {
                    // Do nothing.
                }
            });

        } else {
            // User disconnected

            // Close down the session
            if (mCubicChannel != null && !mCubicChannel.isShutdown()) {
                mCubicChannel.shutdownNow();
                mCubicChannel = null;
            }
            mCubicService = null;

            // Update the UI
            updateConnectUI(false);
        }
    }

    private void updateConnectUI(boolean connected) {
        if (connected) {
            this.buttonConnect.setText(getString(R.string.disconnect)); // If they click on it again, it will disconnect.

            // Prevent the user from changing the connection parameters while connected.
            this.editText_URL.setEnabled(false);
            this.switchSecureConnection.setEnabled(false);
            this.buttonPushToTalk.setEnabled(true);
        } else {
            this.buttonConnect.setText(getString(R.string.connect));

            // Allow the user to chang the connection parameters while disconnected.
            this.editText_URL.setEnabled(true);
            this.switchSecureConnection.setEnabled(true);
            this.buttonPushToTalk.setEnabled(false);  // Must be connected before clicking Transcribe

            // Clear out the model combo box
            if (mAvailableModels != null) {
                mAvailableModels.clear();
            }
            spinnerModels.setAdapter(null);
            spinnerModels.setEnabled(false); // Must be connected before a model can be selected

            // Clear out the version text
            labelVersions.setText(getString(R.string.versions));

            // Clear out the transcription text
            mTranscriptionText.setLength(0);
            textViewResults.setText(getString(R.string.transcriptions_will_appear_here));
        }

    }

    // Recognition streaming stuff

    private StreamObserver<StreamingRecognizeRequest> mRecognitionRequestObserver; // channel to send recognition requests on
    private StreamObserver<RecognitionResponse> mRecognitionResponseObserver = // channel to receive recognition results on
            new StreamObserver<RecognitionResponse>() {
                @Override
                public void onNext(RecognitionResponse value) {
                    Log.i(TAG, "========== New Transcription result available:" + value.toString());
                    for (CubicOuterClass.RecognitionResult r : value.getResultsList()) {
                        if (!r.getIsPartial() && r.getAlternativesCount() > 0) { // Ignore partials.
                            mTranscriptionText.append(r.getAlternatives(0).getTranscript()).append('\n');
                        }
                    }

                    // Update the results on the UI
                    runOnUiThread(() -> textViewResults.setText(mTranscriptionText.toString()));
                }

                @Override
                public void onError(Throwable t) {
                    Log.e(TAG, "=============== Error reading transcription responses.", t);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error reading transcription responses:" + t.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCompleted() {
                    Log.i(TAG, "=============== Transcription results are complete now.");
                }
            };

    // Recognition Thread/Runnable implementing the push-to-talk functionality.
    private HandlerThread mRecognitionThread;
    private Handler mRecognitionHandler;
    private Runnable mRecognitionRunnable = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "starting recognition request");

            if (mSelectedModel == null) {
                Log.e(TAG, "There was no model selected.");
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error starting transcription service, no model selected.", Toast.LENGTH_SHORT).show());
                return;
            }



            // Start the bi-directional streaming.
            mRecognitionRequestObserver = mCubicService.streamingRecognize(mRecognitionResponseObserver);

            // Send the config as the first message, with no audio.
            // Audio is sent in the `mStreamRecognitionRequest` runnable,
            // so don't call setAudio() here.
            StreamingRecognizeRequest configs = StreamingRecognizeRequest.newBuilder()
                    .setConfig(RecognitionConfig.newBuilder()
                            .setModelId(mSelectedModel.getId())
                            .setAudioEncoding(RecognitionConfig.Encoding.RAW_LINEAR16)
                            .setEnableWordTimeOffsets(false)
                            .setEnableRawTranscript(true)
                            .setEnableConfusionNetwork(false)
                            .setEnableWordConfidence(false)
                            .build())
                    .build();
            Log.v(TAG, "Sending configs: " + configs.toString());
            mRecognitionRequestObserver.onNext(configs);

            // Setup audio
            mAudioRecorder = new AudioRecorderWrapper(
                    // Configuration passed to android's AudioRecord object.
                    new AudioRecorderWrapper.Configs()
                            .setBytesBufferSize(SAMPLE_BLOCK_SIZE)
                            .setSampleRate(mSelectedModel.getAttributes().getSampleRate()),
                    // Callback for when we have audio bytes from the mic to stream to CubicSvr.
                    audioBytes -> {
                        ByteString audio = ByteString.copyFrom(audioBytes);
                        // Note: Don't call setConfig()
                        StreamingRecognizeRequest msg = StreamingRecognizeRequest.newBuilder()
                                .setAudio(RecognitionAudio.newBuilder()
                                        .setData(audio)
                                        .build())
                                .build();
                        Log.v(TAG, "Sending Audio: ("+audio.size()+" bytes)");
                        mRecognitionRequestObserver.onNext(msg);
                    }
            );

            // This function will not return until we stop recording (when we let go of the button).
            mAudioRecorder.recordAndroidAudio();
            Log.v(TAG, "Recording audio finished");

            // Close the client->server stream
            Log.v(TAG, "Closing client-side stream");
            mRecognitionRequestObserver.onCompleted();

            Log.v(TAG,  "Shutting down Recognition thread");
        }
    };
}

