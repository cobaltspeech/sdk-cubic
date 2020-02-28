package com.cubic.sdk.model;

import androidx.annotation.NonNull;

import cobaltspeech.cubic.CubicOuterClass;

public abstract class CubicAudioConfiguration<T> {

    private final T mConfiguration;

    CubicAudioConfiguration(@NonNull T configuration) {
        mConfiguration = configuration;
    }

    public abstract String getName();

    public T getConfiguration() {
        return mConfiguration;
    }

    public abstract CubicOuterClass.Model getCubicOuterModel();

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof CubicAudioConfiguration)) return false;
        if (getClass() != o.getClass()) return false;
        CubicAudioConfiguration that = (CubicAudioConfiguration) o;
        return this.equals(that);
    }

    @Override
    public int hashCode() {
        return mConfiguration.hashCode();
    }
}