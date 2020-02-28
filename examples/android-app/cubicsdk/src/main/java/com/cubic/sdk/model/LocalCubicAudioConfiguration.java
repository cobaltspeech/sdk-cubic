package com.cubic.sdk.model;

import androidx.annotation.NonNull;

import com.cubic.sdk.repository.model.Model;

import cobaltspeech.cubic.CubicOuterClass;

public class LocalCubicAudioConfiguration extends CubicAudioConfiguration<Model> {

    private CubicOuterClass.Model mServerAudioConfiguration;

    public LocalCubicAudioConfiguration(@NonNull Model model) {
        super(model);
    }

    public String getName() {
        return getConfiguration().getName();
    }

    @Override
    public CubicOuterClass.Model getCubicOuterModel() {
        return mServerAudioConfiguration;
    }

    public void setServerAudioConfiguration(@NonNull CubicOuterClass.Model configuration) {
        mServerAudioConfiguration = configuration;
    }


    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
