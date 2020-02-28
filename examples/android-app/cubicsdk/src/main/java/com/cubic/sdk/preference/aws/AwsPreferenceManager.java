package com.cubic.sdk.preference.aws;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cubic.sdk.preference.base.BasePrefsManager;

public final class AwsPreferenceManager extends BasePrefsManager implements IAwsPreferenceManager {

    private static final String SHARED_PREFERENCES_NAME = "AWS_PREFERENCE";

    public AwsPreferenceManager(@NonNull Context context) {
        super(context, SHARED_PREFERENCES_NAME);
    }

    @Override
    public void setAWSAccessKeyId(@NonNull String awsAccessKeyId) {
        setParam(AwsPreferenceOptions.Keys.ACCESS_KEY_ID, awsAccessKeyId);
    }

    @Override
    public void setAWSSecretKey(@NonNull String awsSecretKey) {
        setParam(AwsPreferenceOptions.Keys.SECRET_KEY, awsSecretKey);
    }

    @Override
    public void setBucket(@NonNull String bucket) {
        setParam(AwsPreferenceOptions.Keys.BUCKET, bucket);
    }

    @Override
    public String getAWSAccessKeyId() {
        return getParam(AwsPreferenceOptions.Keys.ACCESS_KEY_ID,
                AwsPreferenceOptions.Defaults.ACCESS_KEY_ID);
    }

    @Override
    public String getAWSSecretKey() {
        return getParam(AwsPreferenceOptions.Keys.SECRET_KEY,
                AwsPreferenceOptions.Defaults.SECRET_KEY);
    }

    @Override
    public String getBucket() {
        return getParam(AwsPreferenceOptions.Keys.BUCKET,
                AwsPreferenceOptions.Defaults.BUCKET);
    }
}
