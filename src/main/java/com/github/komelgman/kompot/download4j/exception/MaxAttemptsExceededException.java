package com.github.komelgman.kompot.download4j.exception;

import java.util.concurrent.CancellationException;

public class MaxAttemptsExceededException extends DownloaderException {
    public MaxAttemptsExceededException(int retryCount) {
        super("Maximum number of retry attempts reached: " + retryCount, new CancellationException(), false);
    }
}
