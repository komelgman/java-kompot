package io.github.komelgman.kompot.streams;

import io.github.komelgman.kompot.vfs.PathResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public final class StreamHelper {

    public static StreamProvider newStreamProvider(String file, PathResolver fs) {
        return new StreamProvider() {
            @Override
            public InputStream getInputStream() throws IOException {
                return Files.newInputStream(fs.resolvePath(file));
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return Files.newOutputStream(fs.resolvePath(file));
            }
        };
    }

    private StreamHelper() {
    }
}
