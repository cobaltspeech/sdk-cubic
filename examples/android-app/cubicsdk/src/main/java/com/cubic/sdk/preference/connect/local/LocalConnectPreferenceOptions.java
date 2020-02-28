package com.cubic.sdk.preference.connect.local;

public class LocalConnectPreferenceOptions {

    public interface Keys {
        String HOST = "HOST";
        String IS_SECURE_CONNECTION = "IS_SECURE_CONNECTION";
    }

    public interface Defaults {
        String HOST = "localhost:2727";
        boolean IS_SECURE_CONNECTION = true;
    }
}
