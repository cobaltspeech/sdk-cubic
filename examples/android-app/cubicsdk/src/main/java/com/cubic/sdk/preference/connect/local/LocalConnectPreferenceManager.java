package com.cubic.sdk.preference.connect.local;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cubic.sdk.model.ConnectionConfiguration;
import com.cubic.sdk.preference.base.BasePrefsManager;
import com.cubic.sdk.preference.connect.IConnectPreferenceManager;

public final class LocalConnectPreferenceManager extends BasePrefsManager implements IConnectPreferenceManager {

    private static final String SHARED_PREFERENCES_NAME = "LOCAL_CONNECTION_PREFERENCE";

    public LocalConnectPreferenceManager(@NonNull Context context) {
        super(context, SHARED_PREFERENCES_NAME);
    }

    @Override
    public ConnectionConfiguration getConnectionConfiguration() {
        String host = getParam(LocalConnectPreferenceOptions.Keys.HOST,
                LocalConnectPreferenceOptions.Defaults.HOST);
        boolean isSecure = getParam(LocalConnectPreferenceOptions.Keys.IS_SECURE_CONNECTION,
                LocalConnectPreferenceOptions.Defaults.IS_SECURE_CONNECTION);
        return new ConnectionConfiguration(host, isSecure);
    }

    @Override
    public void saveHost(@NonNull String host) {
        setParam(LocalConnectPreferenceOptions.Keys.HOST, host);
    }

    @Override
    public void saveSecure(boolean isSecure) {
        setParam(LocalConnectPreferenceOptions.Keys.IS_SECURE_CONNECTION, isSecure);
    }
}
