package com.github.komelgman.kompot.download4j;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DownloadWorkerTest {
    @Mock DownloadTask<String> mockTask;
    @Mock DownloadListener<String> mockListener;
    @Mock DownloadSource mockSource;
    @Mock DownloadTarget<String> mockTarget;
    @Mock InputStream mockInputStream;
    @Mock OutputStream mockOutputStream;
    private DownloadWorker<String> downloadWorker;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        when(mockTask.getSource()).thenReturn(mockSource);
        when(mockTask.getTarget()).thenReturn(mockTarget);

        downloadWorker = new DownloadWorker<>(mockTask, mockListener);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void get_ReturnsResultAfterSuccessfulDownload() throws IOException {
        // Given
        String expectedResult = "Downloaded result";
        when(mockInputStream.read(any()))
                .thenReturn(1000)
                .thenReturn(1000)
                .thenReturn(1000)
                .thenReturn(1000)
                .thenReturn(0);
        when(mockTarget.getResult()).thenReturn(expectedResult);
        when(mockSource.getInputStream()).thenReturn(mockInputStream);
        when(mockTarget.getOutputStream()).thenReturn(mockOutputStream);
        when(mockSource.getBufferSize()).thenReturn(1000);

        // When
        String result = downloadWorker.get();

        // Then
        assertEquals(expectedResult, result);
        verify(mockTask, times(1)).initialize();
        verify(mockListener, times(1)).onDownloadStarted(mockTask);
        verify(mockListener, times(1)).onDownloadInitialized(mockTask);
        verify(mockListener, times(4)).onDownloadProgress(eq(mockTask), anyLong());
        verify(mockTarget, times(1)).prepareResult();
        verify(mockListener, times(1)).onDownloadCompleted(mockTask);

        verify(mockSource).getInputStream();
        verify(mockTarget).getOutputStream();
        verify(mockInputStream).close();
        verify(mockOutputStream).close();
    }

//    @Test
//    void get_ReturnsResultAfterDownloadTargetExistsException() {
//        // Given
//        String expectedResult = "Downloaded result";
//        when(mockTarget.getResult()).thenReturn(expectedResult);
//        when(mockSource.getInputStream()).thenReturn(mockInputStream);
//        when(mockTarget.getOutputStream()).thenReturn(mockOutputStream);
//        doThrow(DownloadTargetExistsException.class).when(mockTask).initialize();
//
//        // When
//        String result = downloadWorker.get();
//
//        // Then
//        assertEquals(expectedResult, result);
//        verify(mockListener).onDownloadStarted(mockTask);
//        verify(mockTask).initialize();
//        verify(mockListener).onDownloadInitialized(mockTask);
//        verify(mockListener).onErrorOccurred(mockTask, any(DownloadTargetExistsException.class));
//        verify(mockListener).onDownloadCompleted(mockTask);
//        verify(mockSource).getInputStream();
//        verify(mockTarget).getOutputStream();
//        verify(mockListener, times(2)).onDownloadProgress(mockTask, anyLong());
//        verify(mockTarget).prepareResult();
//        verify(mockInputStream).close();
//        verify(mockOutputStream).close();
//    }
//
//    @Test
//    void get_ReturnsTemporaryResultAfterDownloadTargetWriteException() {
//        // Given
//        String expectedTemporaryResult = "Temporary result";
//        when(mockTarget.getTemporaryResult()).thenReturn(expectedTemporaryResult);
//        when(mockSource.getInputStream()).thenReturn(mockInputStream);
//        when(mockTarget.getOutputStream()).thenReturn(mockOutputStream);
//        doThrow(DownloadTargetWriteException.class).when(mockTask).initialize();
//
//        // When
//        String result = downloadWorker.get();
//
//        // Then
//        assertEquals(expectedTemporaryResult, result);
//        verify(mockListener).onDownloadStarted(mockTask);
//        verify(mockTask).initialize();
//        verify(mockListener).onDownloadInitialized(mockTask);
//        verify(mockListener).onErrorOccurred(mockTask, any(DownloadTargetWriteException.class));
//        verify(mockListener).onDownloadCompleted(mockTask);
//        verify(mockSource).getInputStream();
//        verify(mockTarget).getOutputStream();
//        verify(mockListener, times(2)).onDownloadProgress(mockTask, anyLong());
//        verify(mockInputStream).close();
//        verify(mockOutputStream).close();
//    }
//
//    @Test
//    void get_ThrowsDownloaderExceptionOnIOException() throws IOException {
//        // Given
//        when(mockSource.getInputStream()).thenReturn(mockInputStream);
//        when(mockTarget.getOutputStream()).thenReturn(mockOutputStream);
//        doThrow(IOException.class).when(mockInputStream).read(any(byte[].class));
//
//        // When/Then
//        assertThrows(DownloaderException.class, () -> downloadWorker.get());
//        verify(mockListener).onDownloadStarted(mockTask);
//        verify(mockTask).initialize();
//        verify(mockListener).onDownloadInitialized(mockTask);
//        verify(mockSource).getInputStream();
//        verify(mockTarget).getOutputStream();
//        verify(mockListener).onErrorOccurred(mockTask, any(DownloadOutputException.class));
//        verify(mockListener).onDownloadCompleted(mockTask);
//        verify(mockInputStream).close();
//        verify(mockOutputStream).close();
//    }
}
