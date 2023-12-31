package io.github.komelgman.kompot.download4j;

import io.github.komelgman.kompot.streams.OutputStreamProvider;

public interface DownloadTarget<R> extends OutputStreamProvider {
    void initialize(DownloadSourceInfo sourceInfo);
    long getInitialDataLength();
    void prepareResult();
    String getResultName(boolean isShort);
    String getTemporaryResultName(boolean isShort);
    R getResult();
    R getTemporaryResult();
}
