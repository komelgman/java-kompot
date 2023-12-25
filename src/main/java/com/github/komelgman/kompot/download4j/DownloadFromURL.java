package com.github.komelgman.kompot.download4j;

import com.github.komelgman.kompot.download4j.exception.DownloaderException;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.github.komelgman.kompot.misc.FileName.sanitizeFilename;

@Getter
@Setter
public class DownloadFromURL implements DownloadSource {
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int DEFAULT_RETRY_COUNT = 3;
    public static final int DEFAULT_RETRY_INTERVAL = 1000;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 0;

    private final String url;

    private int bufferSize;
    private int retryCount;
    private int retryInterval;
    private int connectionTimeout;
    private DownloadSourceInfo sourceInfo;
    private long initialDataLength;

    public DownloadFromURL(String url) {
        bufferSize = DEFAULT_BUFFER_SIZE;
        retryCount = DEFAULT_RETRY_COUNT;
        retryInterval = DEFAULT_RETRY_INTERVAL;
        connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        this.url = url;
    }

    void initializeSourceInfo() {
        var tempConnection = openConnection(url, true);
        sourceInfo = new DownloadSourceInfo(url, getNameFromUrlConnection(tempConnection), tempConnection.getContentLengthLong());
        tempConnection.disconnect();
    }

    @Override
    public DownloadSourceInfo getDownloadSourceInfo() {
        if (sourceInfo == null) {
            initializeSourceInfo();
        }

        return sourceInfo;
    }

    @Override
    public void resumeDownload(long initialDataLength) {
        this.initialDataLength = initialDataLength;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        HttpURLConnection downloadFileConnection = openConnection(url);
        if (initialDataLength > 0) {
            downloadFileConnection.setRequestProperty("Range", "bytes=" + initialDataLength + "-" + sourceInfo.dataSize());
        }

        return new BufferedInputStream(downloadFileConnection.getInputStream());
    }

    HttpURLConnection openConnection(String url) {
        return openConnection(url, false);
    }

    HttpURLConnection openConnection(String url, boolean isHead) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(getConnectionTimeout());

            if (isHead) {
                connection.setRequestMethod("HEAD");
            }

            return connection;
        } catch (IOException e) {
            throw new DownloaderException("Can`t establish connection, see cause", e);
        }
    }

    String getNameFromUrlConnection(HttpURLConnection connection) {
        String contentDisposition = connection.getHeaderField("Content-Disposition");
        String result = null;
        if (contentDisposition != null) {
            int startIndex = contentDisposition.indexOf("filename=");
            if (startIndex != -1) {
                startIndex += 9; // Skip "filename="
                int endIndex = contentDisposition.indexOf(";", startIndex);
                if (endIndex != -1) {
                    result = contentDisposition.substring(startIndex, endIndex);
                } else {
                    result = contentDisposition.substring(startIndex);
                }
            }
        }

        if (result != null) {
            result = sanitizeFilename(result);
        }

        if (result != null && result.isBlank()) {
            result = null;
        }

        return result;
    }
}
