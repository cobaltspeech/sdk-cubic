package com.cubic.sdk.repository.model;

import androidx.annotation.NonNull;

public class Model {

    private final String mModelPath;

    private final String mName;

    private final String mConfigFile;
    private final String mGraphFile;
    private final String mNNET3File;

    public Model(@NonNull String name, @NonNull String path) {
        mName = name;
        mModelPath = path;
        mConfigFile = "model.config";
        mGraphFile = "graph";
        mNNET3File = "nnet3_online";
    }

    public String getName() {
        return mName;
    }

    public String getModelPath() {
        return mModelPath + mName;
    }

    public String getConfigFile() {
        return mConfigFile;
    }

    public String getGraphFile() {
        return mGraphFile;
    }

    public String getGraphPath() {
        return getModelPath();
    }

    public String getNNET3File() {
        return mNNET3File;
    }

    public String getNNET3Path() {
        return getModelPath() + "/am";
    }
}
