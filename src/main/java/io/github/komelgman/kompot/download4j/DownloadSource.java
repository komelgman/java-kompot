package io.github.komelgman.kompot.download4j;

import io.github.komelgman.kompot.streams.InputStreamProvider;

public interface DownloadSource extends InputStreamProvider {
    void resumeDownload(long initialDataSize);

    default int getRetryCount() {
        return DownloadFromURL.DEFAULT_RETRY_COUNT;
    }

    default int getRetryInterval() {
        return DownloadFromURL.DEFAULT_RETRY_INTERVAL;
    }

    default int getBufferSize() {
        return DownloadFromURL.DEFAULT_BUFFER_SIZE;
    }

    default int getConnectionTimeout() {
        return DownloadFromURL.DEFAULT_CONNECTION_TIMEOUT;
    }

    DownloadSourceInfo getDownloadSourceInfo();
}
