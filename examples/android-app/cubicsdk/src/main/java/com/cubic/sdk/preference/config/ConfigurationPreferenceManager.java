package com.cubic.sdk.preference.config;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cubic.sdk.preference.base.BasePrefsManager;

public final class ConfigurationPreferenceManager extends BasePrefsManager implements IConfigurationPreferenceManager {

    private static final String SHARED_PREFERENCES_NAME = "CONFIGURATION_PREFERENCE";

    public ConfigurationPreferenceManager(@NonNull Context context) {
        super(context, SHARED_PREFERENCES_NAME);
    }

    @Override
    public String getLicense() {
        return getParam(ConfigurationPreferenceOptions.Keys.LICENSE,
                ConfigurationPreferenceOptions.Defaults.LICENSE);
    }

    @Override
    public void setLicense(@NonNull String license) {
        setParam(ConfigurationPreferenceOptions.Keys.LICENSE, license);
    }
}
