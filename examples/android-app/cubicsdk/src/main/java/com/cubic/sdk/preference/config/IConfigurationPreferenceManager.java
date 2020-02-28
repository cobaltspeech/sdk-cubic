package com.cubic.sdk.preference.config;

import androidx.annotation.NonNull;

public interface IConfigurationPreferenceManager {

    String getLicense();

    void setLicense(@NonNull String license);
}
