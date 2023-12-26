package io.github.komelgman.kompot.vfs;

import java.nio.file.FileSystem;
import java.nio.file.Path;

public class ZipPathResolver implements PathResolver {
    private final FileSystem zipFileSystem;

    public ZipPathResolver(FileSystem zipFileSystem) {
        this.zipFileSystem = zipFileSystem;
    }

    @Override
    public Path resolvePath(String path) {
        return zipFileSystem.getPath(path);
    }
}
