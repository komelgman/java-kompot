package com.github.komelgman.kompot.download4j;

import com.github.komelgman.kompot.download4j.exception.DownloadResumeException;
import com.github.komelgman.kompot.download4j.exception.DownloadTargetExistsException;
import com.github.komelgman.kompot.download4j.exception.DownloadTargetWriteException;
import com.github.komelgman.kompot.misc.HumanReadable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static java.text.MessageFormat.format;

@Slf4j
@RequiredArgsConstructor
public class DownloadToFile implements DownloadTarget<File> {
    private final File outputDir;

    @Getter
    private long initialDataLength;
    private File tempFile;
    private File resultFile;

    public DownloadToFile() {
        this(new File(System.getProperty("java.io.tmpdir")));
    }

    @Override
    public void initialize(DownloadSourceInfo info) {
        tempFile = getTempFile(info.id());
        resultFile = getResultFile(info.id(), info.dataName());
        initialDataLength = tempFile.length();

        if (resultFile.exists()) {
            var message = "Downloading skipped, already presented file will be used.";
            var cause = new IllegalStateException(format(
                    "File \"{0}\" already exists in folder {1}",
                    resultFile.getName(),
                    resultFile.getParentFile().getAbsolutePath()
            ));

            throw new DownloadTargetExistsException(message, cause);
        }

        if (info.dataSize() < initialDataLength) {
            var message = format(
                    "Something went wrong with file \"{0}\"",
                    tempFile.getAbsolutePath()
            );
            var cause = new IllegalStateException(format(
                    "Incorrect temp file size, requested data size is {0} but {1} found",
                    HumanReadable.byteCount(info.dataSize()),
                    HumanReadable.byteCount(initialDataLength)
            ));

            throw new DownloadResumeException(message, cause);
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return new BufferedOutputStream(new FileOutputStream(tempFile, true));
    }

    @Override
    public void prepareResult() {
        try {
            Files.move(tempFile.toPath(), resultFile.toPath());
        } catch (IOException e) {
            var message = format(
                    "Can`t rename file {0} to {1}",
                    tempFile.getAbsolutePath(),
                    resultFile.getAbsolutePath()
            );

            throw new DownloadTargetWriteException(message, e);
        }
    }

    @Override
    public File getResult() {
        return resultFile;
    }

    @Override
    public File getTemporaryResult() {
        return tempFile;
    }

    @Override
    public String getResultName(boolean isShort) {
        return isShort ? resultFile.getName() : resultFile.getAbsolutePath();
    }

    @Override
    public String getTemporaryResultName(boolean isShort) {
        return isShort ? tempFile.getName() : tempFile.getAbsolutePath();
    }

    File getTempFile(String sourceId) {
        return getOutputFile(fromSourceId(sourceId));
    }

    File getResultFile(String sourceId, String dataName) {
        String fileName = dataName;
        if (fileName == null) {
            fileName = fromSourceId(sourceId, "downloaded-unknown");
        }

        return getOutputFile(fileName);
    }

    File getOutputFile(String fileName) {
        return new File(outputDir, fileName);
    }

    String fromSourceId(String sourceId) {
        return fromSourceId(sourceId, "download4j");
    }

    String fromSourceId(String sourceId, String extension) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(sourceId.getBytes());
            String hex = HexFormat.of().formatHex(messageDigest.digest());
            String stringHash = hex.substring(hex.length() - 12);

            return "file-" + stringHash + "." + extension;
        } catch (NoSuchAlgorithmException e) {
            if (log.isDebugEnabled()) {
                log.debug("Oops!", e);
            } else {
                log.warn(e.getMessage());
            }

            return "file-" + HexFormat.of().toHexDigits(sourceId.hashCode()) + "." + extension;
        }
    }
}
