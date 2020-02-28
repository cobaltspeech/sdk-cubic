package com.cubic.sdk.preference.aws;

import androidx.annotation.NonNull;

public interface IAwsPreferenceManager {

    void setAWSAccessKeyId(@NonNull String awsAccessKeyId);

    void setAWSSecretKey(@NonNull String awsSecretKey);

    void setBucket(@NonNull String bucket);

    String getAWSAccessKeyId();

    String getAWSSecretKey();

    String getBucket();
}
