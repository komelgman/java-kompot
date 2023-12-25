package com.github.komelgman.kompot.download4j;

import com.github.komelgman.kompot.download4j.exception.DownloadTargetExistsException;
import com.github.komelgman.kompot.download4j.exception.DownloadTargetWriteException;
import com.github.komelgman.kompot.download4j.exception.DownloaderException;
import com.github.komelgman.kompot.download4j.exception.DownloadOutputException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Supplier;

@Getter
class DownloadWorker<R> implements Supplier<R> {
    private final DownloadTask<R> task;
    private final DownloadListener<R> listener;
    private final DownloadSource source;
    private final DownloadTarget<R> target;

    public DownloadWorker(DownloadTask<R> task, DownloadListener<R> listener) {
        this.task = task;
        this.listener = listener;

        this.source = task.getSource();
        this.target = task.getTarget();
    }

    @Override
    public R get() {
        R result;
        listener.onDownloadStarted(task);

        try {
            task.initialize();
            listener.onDownloadInitialized(task);
            download();
            result = target.getResult();
        } catch (DownloadTargetExistsException e) {
            listener.onErrorOccurred(task, e);
            result = target.getResult();
        } catch (DownloadTargetWriteException e) {
            listener.onErrorOccurred(task, e);
            result = target.getTemporaryResult();
        }

        listener.onDownloadCompleted(task);
        return result;
    }

    void download() {
        try (
                InputStream is = source.getInputStream();
                OutputStream os = target.getOutputStream()
        ) {
            byte[] buffer = new byte[source.getBufferSize()];
            int bytesRead;
            long bytesCount = target.getInitialDataLength();
            while ((bytesRead = is.read(buffer)) > 0) {
                os.write(buffer, 0, bytesRead);
                bytesCount += bytesRead;

                listener.onDownloadProgress(task, bytesCount);
            }

            target.prepareResult();
        } catch (IOException e) {
            throw new DownloaderException("Something went wrong while data downloading, see cause", e);
        }
    }
}
