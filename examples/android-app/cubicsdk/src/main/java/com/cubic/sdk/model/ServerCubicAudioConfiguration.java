package com.cubic.sdk.model;

import androidx.annotation.NonNull;

import cobaltspeech.cubic.CubicOuterClass;

public class ServerCubicAudioConfiguration extends CubicAudioConfiguration<CubicOuterClass.Model> {

    public ServerCubicAudioConfiguration(@NonNull CubicOuterClass.Model configuration) {
        super(configuration);
    }

    public String getName() {
        return getConfiguration().getName();
    }

    @Override
    public CubicOuterClass.Model getCubicOuterModel() {
        return getConfiguration();
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
