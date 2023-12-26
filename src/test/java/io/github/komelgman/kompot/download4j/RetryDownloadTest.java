package io.github.komelgman.kompot.download4j;

import io.github.komelgman.kompot.download4j.exception.DownloadAbortedException;
import io.github.komelgman.kompot.download4j.exception.DownloaderException;
import io.github.komelgman.kompot.download4j.exception.MaxAttemptsExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetryDownloadTest {
    @Mock CompletableFuture<File> completableFuture;
    @Mock AtomicReference<CompletableFuture<File>> taskRef;
    @Mock DownloadWorker<File> downloadWorker;
    @Mock DownloadSource source;
    @Mock DownloadTask<File> task;
    @Mock DownloadListener<File> listener;

    RetryDownload<File> retryDownload;

    @BeforeEach
    void setUp() {
        when(taskRef.get()).thenReturn(completableFuture);

        when(source.getRetryCount()).thenReturn(3);

        when(task.getSource()).thenReturn(source);

        when(downloadWorker.getTask()).thenReturn(task);
        when(downloadWorker.getListener()).thenReturn(listener);

        retryDownload = new RetryDownload<>(taskRef, downloadWorker);
    }

    @Test
    void testRetry_SuccessfulDownload() {
        Supplier<File> supplier = () -> {
            // Simulate successful download
            return new File("result.txt");
        };

        File result = retryDownload.retry(supplier).get();

        verify(listener, never()).onErrorOccurred(any(), any(DownloaderException.class));
        verify(listener, never()).onRetry(any(), anyInt(), anyInt());
        assertEquals("result.txt", result.getName());
    }

    @Test
    void testRetry_RetryableException_OneTime(@Mock Supplier<File> supplier) {
        when(source.getRetryCount()).thenReturn(3);

        when(supplier.get())
                .thenThrow(new DownloaderException("retryable", true))
                .thenReturn(new File("result.txt"));

        File result = retryDownload.retry(supplier).get();

        assertEquals("result.txt", result.getName());
        verify(listener, times(1)).onRetry(any(), anyInt(), anyInt());
        verify(listener, times(1)).onErrorOccurred(any(), any(DownloaderException.class));
        verify(listener, never()).onDownloadCompleted(any());
    }

    @Test
    void testRetry_RetryableException_MaxAttempts(@Mock Supplier<File> supplier) {
        when(source.getRetryCount()).thenReturn(3);

        when(supplier.get()).thenThrow(new DownloaderException("retryable", true));

        Supplier<File> retry = retryDownload.retry(supplier);
        assertThrows(MaxAttemptsExceededException.class, retry::get);

        verify(listener, times(source.getRetryCount())).onRetry(any(), anyInt(), anyInt());
        verify(listener, times(source.getRetryCount() + 1)).onErrorOccurred(any(), any(DownloaderException.class));
        verify(listener, never()).onDownloadCompleted(any());
    }

    @Test
    void testRetry_NonRetryableException() {
        Supplier<File> supplier = () -> {
            // Simulate non-retryable exception
            throw new DownloaderException("Non-retryable exception", false);
        };

        Supplier<File> retry = retryDownload.retry(supplier);
        assertThrows(DownloaderException.class, retry::get);

        verify(listener, times( 1)).onErrorOccurred(any(), any(DownloaderException.class));
        verify(listener, never()).onRetry(any(), anyInt(), anyInt());
        verify(listener, never()).onDownloadCompleted(any());
    }

    @Test
    void testRetry_NonDownloaderException() {
        Supplier<File> supplier = () -> {
            // Simulate exception downloader
            throw new IllegalStateException("Some exception");
        };

        Supplier<File> retry = retryDownload.retry(supplier);
        assertThrows(DownloaderException.class, retry::get);

        verify(listener, times( 1)).onErrorOccurred(any(), any(DownloaderException.class));
        verify(listener, never()).onRetry(any(), anyInt(), anyInt());
        verify(listener, never()).onDownloadCompleted(any());
    }

    @Test
    void testRetry_DownloadAbortedException() {
        Supplier<File> supplier = () -> {
            // Simulate retryable exception
            throw new DownloaderException("Retryable exception", true);
        };

        when(taskRef.get().isCancelled())
                .thenReturn(false)
                .thenReturn(true);

        Supplier<File> retry = retryDownload.retry(supplier);
        assertThrows(DownloadAbortedException.class, retry::get);

        verify(listener, times(1)).onRetry(any(), anyInt(), anyInt());
        verify(listener, times(2)).onErrorOccurred(any(), any(DownloaderException.class));
        verify(listener, never()).onDownloadCompleted(any());
    }
}
