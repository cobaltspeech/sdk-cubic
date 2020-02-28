package com.cubic.sdk.common;

import android.util.Log;

import androidx.annotation.NonNull;

import com.cubic.sdk.BuildConfig;

public class Logger {

    private static final String TAG = "COM.CUBIC.SDK";
    private static IExceptionTransfer mIExceptionTransfer;

    public static void debug(String message) {
        if (BuildConfig.DEBUG)
            Log.d(TAG, String.valueOf(message));
    }

    public static void exception(@NonNull Throwable e) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, String.valueOf(e.getMessage()));
        else if (mIExceptionTransfer != null)
            mIExceptionTransfer.exception(e);
    }

    public static void plantIExceptionTransfer(IExceptionTransfer mIExceptionTransfer) {
        Logger.mIExceptionTransfer = mIExceptionTransfer;
    }

    public interface IExceptionTransfer {
        void exception(@NonNull Throwable e);
    }
}
