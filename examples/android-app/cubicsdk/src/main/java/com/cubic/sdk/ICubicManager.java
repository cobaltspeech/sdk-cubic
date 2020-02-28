package com.cubic.sdk;

import com.cubic.sdk.model.CubicAudioConfiguration;

import java.util.List;

public interface ICubicManager<T extends CubicAudioConfiguration> {

    void connect();

    void disconnect();

    void setAudioConfiguration(T configuration);

    List<T> getAudioConfigurations();

    void talk();

    void stopTalk();
}
