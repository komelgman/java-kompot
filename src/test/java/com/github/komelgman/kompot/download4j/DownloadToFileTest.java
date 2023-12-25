package com.github.komelgman.kompot.download4j;

import com.github.komelgman.kompot.download4j.exception.DownloadResumeException;
import com.github.komelgman.kompot.download4j.exception.DownloadTargetExistsException;
import com.github.komelgman.kompot.download4j.exception.DownloadTargetWriteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DownloadToFileTest {
    @TempDir Path tempDir;
    @Mock File tempFile;
    @Mock File resultFile;

    private DownloadSourceInfo sourceInfo;
    private DownloadToFile downloadTarget;

    @BeforeEach
    void setUp() {
        sourceInfo = new DownloadSourceInfo("https://some.org/blob.zip", "blob.zip", 6000L);
        downloadTarget = new DownloadToFile(tempDir.toFile()) {
            @Override
            File getTempFile(String sourceId) {
                return tempFile;
            }

            @Override
            File getResultFile(String sourceId, String dataName) {
                return resultFile;
            }
        };
    }

    @Test
    void initialize_ExistingResultFile_ThrowsDownloadTargetExistsException() {
        when(resultFile.exists()).thenReturn(true);
        when(resultFile.getParentFile()).thenReturn(tempDir.toFile());

        assertThrows(DownloadTargetExistsException.class, () -> downloadTarget.initialize(sourceInfo));
    }

    @Test
    void initialize_IncorrectTempFileSize_ThrowsDownloadResumeException() {
        when(tempFile.length()).thenReturn(sourceInfo.dataSize() + 1);

        assertThrows(DownloadResumeException.class, () -> downloadTarget.initialize(sourceInfo));
    }

    @Test
    void prepareResult_IOExceptionWhenCopy_ThrowsDownloadTargetWriteException() {
        when(tempFile.getAbsolutePath()).thenReturn("");
        when(resultFile.getAbsolutePath()).thenReturn("");
        when(tempFile.toPath()).then(__ -> { throw new IOException("oops"); });

        downloadTarget.initialize(sourceInfo);

        assertThrows(DownloadTargetWriteException.class, () -> downloadTarget.prepareResult());
    }
}
