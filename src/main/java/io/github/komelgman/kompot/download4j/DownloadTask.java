package io.github.komelgman.kompot.download4j;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DownloadTask<R> {
    private final DownloadSource source;
    private final DownloadTarget<R> target;

    public void initialize() {
        target.initialize(source.getDownloadSourceInfo());

        if (target.getInitialDataLength() > 0) {
            source.resumeDownload(target.getInitialDataLength());
        }
    }
}
