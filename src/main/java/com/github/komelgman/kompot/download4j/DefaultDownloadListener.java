package com.github.komelgman.kompot.download4j;

import java.io.File;
import com.github.komelgman.kompot.download4j.DownloadListener;
import com.github.komelgman.kompot.download4j.DownloadTarget;
import com.github.komelgman.kompot.download4j.DownloadTask;
import com.github.komelgman.kompot.download4j.exception.DownloaderException;

public class DefaultDownloadListener implements DownloadListener<File> {
    @Override
    public void onDownloadInitialized(DownloadTask<File> task) {
        // do nothing
    }

    @Override
    public void onDownloadStarted(DownloadTask<File> task) {
        // do nothing
    }

    @Override
    public void onDownloadProgress(DownloadTask<File> task, long downloadedDataLength) {
        // do nothing
    }

    @Override
    public void onDownloadCompleted(DownloadTask<File> task) {
        // do nothing
    }

    @Override
    public void onErrorOccurred(DownloadTask<File> task, DownloaderException de) {
        // do nothing
    }

    @Override
    public void onRetry(DownloadTask<File> task, int attempt, int retryCount) {
        // do nothing
    }
}
