package com.cubic.sdk.preference.connect.remote;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cubic.sdk.model.ConnectionConfiguration;
import com.cubic.sdk.preference.base.BasePrefsManager;
import com.cubic.sdk.preference.connect.IConnectPreferenceManager;

public final class RemoteConnecPreferenceManager extends BasePrefsManager implements IConnectPreferenceManager {

    private static final String SHARED_PREFERENCES_NAME = "REMOTE_CONNECTION_PREFERENCE";

    public RemoteConnecPreferenceManager(@NonNull Context context) {
        super(context, SHARED_PREFERENCES_NAME);
    }

    @Override
    public ConnectionConfiguration getConnectionConfiguration() {
        String host = getParam(RemoteConnecPreferenceOptions.Keys.HOST,
                RemoteConnecPreferenceOptions.Defaults.HOST);
        boolean isSecure = getParam(RemoteConnecPreferenceOptions.Keys.IS_SECURE_CONNECTION,
                RemoteConnecPreferenceOptions.Defaults.IS_SECURE_CONNECTION);
        return new ConnectionConfiguration(host, isSecure);
    }

    @Override
    public void saveHost(@NonNull String host) {
        setParam(RemoteConnecPreferenceOptions.Keys.HOST, host);
    }

    @Override
    public void saveSecure(boolean isSecure) {
        setParam(RemoteConnecPreferenceOptions.Keys.IS_SECURE_CONNECTION, isSecure);
    }
}
