package com.cubic.example;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.cubic.sdk.common.Logger;

import io.fabric.sdk.android.Fabric;

public class AppDelegate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());
            Logger.plantIExceptionTransfer(Crashlytics::logException);
        }
    }
}
