package com.cubic.sdk.repository.aws;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.mobileconnectors.s3.transfermanager.MultipleFileDownload;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import com.cubic.sdk.preference.aws.AwsPreferenceManager;
import com.cubic.sdk.preference.aws.IAwsPreferenceManager;

import java.io.File;

import io.reactivex.subjects.SingleSubject;

public class AwsClient implements IAwsClient {

    private final Context mContext;
    private final IAwsPreferenceManager mIAwsPreferenceManager;
    private AmazonS3Client mAmazonS3Client;

    public AwsClient(@NonNull Context context) {
        mContext = context;
        mIAwsPreferenceManager = new AwsPreferenceManager(context);
        mContext.startService(new Intent(mContext, TransferService.class));
    }

    private AmazonS3Client getAmazonS3Client() {
        if (mAmazonS3Client == null) {
            mAmazonS3Client = new AmazonS3Client(new AWSCredentials() {
                @Override
                public String getAWSAccessKeyId() {
                    return mIAwsPreferenceManager.getAWSAccessKeyId();
                }

                @Override
                public String getAWSSecretKey() {
                    return mIAwsPreferenceManager.getAWSSecretKey();
                }
            });
        }
        return mAmazonS3Client;
    }

    private void moveDir(File from, File to) {

    }

    @Override
    public void downloadFile(@NonNull String pathFile,
                             @NonNull String outFolder,
                             @NonNull String fileName,
                             @NonNull SingleSubject callback) {
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(mContext)
                        .s3Client(getAmazonS3Client())
                        .defaultBucket(mIAwsPreferenceManager.getBucket())
                        .build();

        TransferObserver downloadObserver = transferUtility
                .download(
                        pathFile + "/" + fileName,
                        new File(outFolder + fileName)
                );
        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state)
                    callback.onSuccess(true);
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
                callback.onError(ex);
            }
        });
    }

    @Override
    public void downloadFolder(@NonNull String pathFolder,
                               @NonNull String outFolder,
                               @NonNull String folderName) throws InterruptedException {
        TransferManager transferManager = new TransferManager(getAmazonS3Client());
        MultipleFileDownload download = transferManager.downloadDirectory(
                mIAwsPreferenceManager.getBucket(),
                pathFolder + File.separatorChar + folderName,
                new File(outFolder));

        download.addProgressListener((ProgressListener) progressEvent -> {
            if (progressEvent.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE
                    && download.isDone()) {
                File from = new File(outFolder + File.separatorChar + pathFolder, folderName);
                File to = new File(outFolder + File.separatorChar, folderName);
                boolean b = from.renameTo(to);
//                if (!from.renameTo(to)) throw new RuntimeException("File not created: " + to);
            }
        });
        download.waitForCompletion();
    }
}
