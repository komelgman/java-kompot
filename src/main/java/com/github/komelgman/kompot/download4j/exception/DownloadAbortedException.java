package com.github.komelgman.kompot.download4j.exception;

import java.util.concurrent.CancellationException;

public class DownloadAbortedException extends DownloaderException {
    public DownloadAbortedException() {
        super("Downloading was aborted.", new CancellationException(), false);
    }
}
