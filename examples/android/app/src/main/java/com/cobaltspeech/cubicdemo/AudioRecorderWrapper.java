// Copyright (2018) Cobalt Speech and Language, Inc. All rights reserved.

package com.cobaltspeech.cubicdemo;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import java.util.Locale;

@SuppressWarnings("unused")
class AudioRecorderWrapper {
    private static final String TAG = AudioRecorderWrapper.class.getSimpleName();

    @SuppressWarnings({"unused", "SameParameterValue"})
    static class Configs {
        int BufferSizeForBytes; // Must be an even number, to support older android devices using the short-based audio recording api calls.
        int SampleRate;

        Configs setBytesBufferSize(int bufferSize) {
            BufferSizeForBytes = bufferSize;
            return this;
        }

        Configs setSampleRate(int sampleRate) {
            SampleRate = sampleRate;
            return this;
        }

        private int getBufferSizeInBytes() {
            return BufferSizeForBytes;
        }

        private int getBufferSizeInShorts() {
            return BufferSizeForBytes / 2;
        }
    }

    // Provides a way to call items in the CubicManager without directly linking the two.
    public interface IAudioReadyCallback {
        // Note: this callback is done on the same thread that recordAndroidAudio is called with.
        void audioReady(byte[] audioBytes);
    }

    AudioRecorderWrapper(Configs cfg, IAudioReadyCallback callback) {
        this.cfg = cfg;
        this.callback = callback;
        Log.v(TAG, String.format(Locale.US, "Creating wrapper with size:%d and rate:%d", cfg.BufferSizeForBytes, cfg.SampleRate));
    }

    private Configs cfg;
    private IAudioReadyCallback callback;
    private boolean isRunning = false;

    void stopRunning() {
        isRunning = false;
    }

    @SuppressLint("ObsoleteSdkInt")
    void recordAndroidAudio() {
        isRunning = true;

        byte[] audio = new byte[cfg.getBufferSizeInBytes()];
        short[] audioShorts = new short[cfg.getBufferSizeInShorts()];

        AudioRecord recorder = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                cfg.SampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                cfg.getBufferSizeInBytes());

        recorder.startRecording();

        // While the user hasn't canceled the recording.
        while (isRunning) {
            // Read audio bytes from the microphone
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // User the newer API if we can.
                recorder.read(audio, 0, cfg.getBufferSizeInBytes(), AudioRecord.READ_BLOCKING);
            } else {
                // Get exactly AUDIO_BUFF_SIZE bytes, in units of shorts
                int cntRead = 0;
                while (cntRead < cfg.getBufferSizeInShorts() && isRunning) {
                    // Note: if nRead is negative, that indicates an error, but I haven't seen that happen yet.
                    int nRead = recorder.read(audioShorts, cntRead, cfg.getBufferSizeInShorts() - cntRead);
                    cntRead += nRead;
                }
                // Once we've read the right number of shorts, push all of those values to the audio byte[]
                for (int i = 0; i < cfg.getBufferSizeInShorts(); i++) {
                    audio[(i * 2)] = (byte) (audioShorts[i] & 0xff);
                    audio[(i * 2) + 1] = (byte) ((audioShorts[i] >> 8) & 0xff);
                }
            }

            // Let the caller know we have a new section of audio ready to be read.
            callback.audioReady(audio);
        }

        // Shut down the audio recorder instance.
        recorder.stop();
    }
}
