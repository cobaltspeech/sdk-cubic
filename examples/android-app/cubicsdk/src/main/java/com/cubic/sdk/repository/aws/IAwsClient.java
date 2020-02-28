package com.cubic.sdk.repository.aws;

import androidx.annotation.NonNull;

import java.io.File;

import io.reactivex.subjects.SingleSubject;

public interface IAwsClient {

    void downloadFile(@NonNull String pathFile,
                      @NonNull String outFolder,
                      @NonNull String fileName,
                      SingleSubject<File> ss);

    void downloadFolder(@NonNull String pathFolder,
                        @NonNull String outFolder,
                        @NonNull String folderName) throws InterruptedException;
}
