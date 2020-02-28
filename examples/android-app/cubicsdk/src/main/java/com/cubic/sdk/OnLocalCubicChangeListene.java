package com.cubic.sdk;

import androidx.annotation.NonNull;

import com.cubic.sdk.model.LocalCubicAudioConfiguration;

public interface OnLocalCubicChangeListene extends OnCubicChangeListener<LocalCubicAudioConfiguration> {

    void onLoadingModel(@NonNull String state);
}
