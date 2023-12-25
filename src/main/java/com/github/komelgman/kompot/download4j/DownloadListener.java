package com.github.komelgman.kompot.download4j;

import com.github.komelgman.kompot.download4j.exception.DownloaderException;

public interface DownloadListener<R> {
    void onDownloadInitialized(DownloadTask<R> task);

    void onDownloadStarted(DownloadTask<R> task);

    void onDownloadProgress(DownloadTask<R> task, long downloadedDataLength);

    void onDownloadCompleted(DownloadTask<R> task);

    void onErrorOccurred(DownloadTask<R> task, DownloaderException de);

    void onRetry(DownloadTask<R> task, int attempt, int retryCount);
}
