package com.github.komelgman.kompot.download4j.exception;

public class DownloadOutputException extends DownloaderException {
    public DownloadOutputException(String message, Throwable cause) {
        super(message, cause, false);
    }
}
