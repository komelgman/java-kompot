package com.github.komelgman.kompot.vfs;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;

public final class ZipHelper {
    public static FileSystemProvider getZipFSProvider() {
        FileSystemProvider result = null;
        for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
            if ("jar".equals(provider.getScheme())) {
                result = provider;
                break;
            }
        }

        if (result == null) {
            throw new IllegalStateException("ZIP filesystem provider is not installed");
        }

        return result;
    }

    public static void createNewZipFile(Path path) {
        createNewZipFile(path, false);
    }

    public static void createNewZipFile(Path path, boolean eraseIfExists) {
        if (Files.exists(path)) {
            String errorMessage =
                    String.format("Can't create empty zip file, it's already exists at path %s",
                            path);

            if (eraseIfExists) {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    throw new IllegalStateException(errorMessage, e);
                }
            } else {
                throw new IllegalStateException(errorMessage);
            }
        }

        try {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            Map<String, Object> env = new HashMap<>();
            env.put("create", true);

            FileSystem fs = ZipHelper.getZipFSProvider().newFileSystem(path, env);
            fs.close();
        } catch (IOException e) {
            throw new IllegalStateException(
                    String.format("Can't create empty zip file at path %s", path), e);
        }
    }

    private ZipHelper() {
    }
}
