package io.github.komelgman.kompot.vfs;

import java.nio.file.Path;

public interface PathResolver {
    Path resolvePath(String path);
}
