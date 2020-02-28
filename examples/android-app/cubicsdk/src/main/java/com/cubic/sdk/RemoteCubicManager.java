package com.cubic.sdk;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.cubic.sdk.model.ServerCubicAudioConfiguration;
import com.cubic.sdk.preference.connect.remote.RemoteConnecPreferenceManager;

import java.util.List;

import cobaltspeech.cubic.CubicOuterClass;

public final class RemoteCubicManager extends BaseCubicManager<ServerCubicAudioConfiguration> {

    public RemoteCubicManager(@NonNull Context context,
                              @NonNull OnCubicChangeListener listener,
                              Lifecycle lifecycle) {
        super(
                context,
                new RemoteConnecPreferenceManager(context),
                listener,
                lifecycle
        );
    }

    public RemoteCubicManager(@NonNull Context context,
                              @NonNull OnCubicChangeListener listener) {
        this(context,
                listener,
                null
        );
    }

    @Override
    protected void onCubicServiceStart(@NonNull CubicOuterClass.ListModelsResponse value) {
        List<CubicOuterClass.Model> list = value.getModelsList();
        mCubicAudioConfigurations.clear();
        for (CubicOuterClass.Model model : list) {
            mCubicAudioConfigurations.add(new ServerCubicAudioConfiguration(model));
        }
        mOnCubicChangeListener.onGetAudioConfigurations(mCubicAudioConfigurations);

        if (!mCubicAudioConfigurations.isEmpty())
            setAudioConfiguration(mCubicAudioConfigurations.get(0));
    }

    @Override
    public void disconnect() {
        super.disconnect();
        mCubicAudioConfigurations.clear();
    }
}
