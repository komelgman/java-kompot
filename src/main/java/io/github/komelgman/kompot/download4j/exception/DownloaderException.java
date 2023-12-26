package io.github.komelgman.kompot.download4j.exception;

import lombok.Getter;

@Getter
public class DownloaderException extends RuntimeException {
    private final boolean retryable;

    public DownloaderException(String message, Throwable cause) {
        this(message, cause, true);
    }

    public DownloaderException(String message, boolean retryable) {
        super(message);
        this.retryable = retryable;
    }

    public DownloaderException(String message, Throwable cause, boolean retryable) {
        super(message, cause);
        this.retryable = retryable;
    }
}
