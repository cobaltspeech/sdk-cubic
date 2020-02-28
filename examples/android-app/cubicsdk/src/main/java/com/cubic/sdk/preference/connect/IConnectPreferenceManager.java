package com.cubic.sdk.preference.connect;

import androidx.annotation.NonNull;

import com.cubic.sdk.model.ConnectionConfiguration;

public interface IConnectPreferenceManager {

    ConnectionConfiguration getConnectionConfiguration();

    void saveHost(@NonNull String host);

    void saveSecure(boolean isSecure);
}
