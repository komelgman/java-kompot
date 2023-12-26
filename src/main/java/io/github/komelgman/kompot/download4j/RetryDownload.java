package io.github.komelgman.kompot.download4j;

import io.github.komelgman.kompot.download4j.exception.DownloadAbortedException;
import io.github.komelgman.kompot.download4j.exception.DownloaderException;
import io.github.komelgman.kompot.download4j.exception.MaxAttemptsExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
class RetryDownload<R> implements Supplier<R> {
    private final AtomicReference<CompletableFuture<R>> taskRef;
    private final DownloadWorker<R> downloadWorker;

    @Override
    public R get() {
        return retry(downloadWorker).get();
    }

    public Supplier<R> retry(Supplier<R> supplier) {
        var task = downloadWorker.getTask();
        var source = task.getSource();
        var listener = downloadWorker.getListener();

        return () -> {
            int attempt = 0;

            while (attempt < source.getRetryCount()) {
                if (taskRef.get() != null && taskRef.get().isCancelled()) {
                    break;
                }

                try {
                    return supplier.get();
                } catch (Exception e) {
                    attempt++;
                    handleDownloadException(e, attempt);
                }
            }

            var de = attempt < source.getRetryCount()
                    ? new DownloadAbortedException()
                    : new MaxAttemptsExceededException(source.getRetryCount());

            listener.onErrorOccurred(task, de);
            throw de;
        };
    }

    void handleDownloadException(Exception e, int attempt) {
        var task = downloadWorker.getTask();
        var source = task.getSource();
        var listener = downloadWorker.getListener();

        if (e instanceof DownloaderException de && de.isRetryable()) {
            listener.onErrorOccurred(task, de);
            listener.onRetry(task, attempt, source.getRetryCount());

            try {
                TimeUnit.MILLISECONDS.sleep(source.getRetryInterval());
            } catch (InterruptedException ex) {
                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    var abortedException = new DownloadAbortedException();
                    abortedException.initCause(ex);
                    throw abortedException;
                }
            }
        } else if (e instanceof DownloaderException de) {
            listener.onErrorOccurred(task, de);
            throw de;
        } else {
            var de = new DownloaderException("Unpredicted exception was thrown, see cause", e, false);
            listener.onErrorOccurred(task, de);
            throw de;
        }
    }
}
