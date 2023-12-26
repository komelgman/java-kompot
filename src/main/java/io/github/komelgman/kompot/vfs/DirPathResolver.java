package io.github.komelgman.kompot.vfs;

import java.nio.file.Path;

public class DirPathResolver implements PathResolver {
    private static final Path FICTIVE_ROOT = Path.of("x:/");

    private final Path root;

    public DirPathResolver(Path root) {
        this.root = root;
    }

    @Override
    public Path resolvePath(String path) {
        Path result = root.resolve(FICTIVE_ROOT.relativize(FICTIVE_ROOT.resolve(Path.of(path))))
                .normalize();

        if (!result.startsWith(root)) {
            throw new IllegalStateException(
                    String.format("Wrong path: Retrieved path %s should contains root path %s",
                            result,
                            root
                    )
            );
        }

        return result;
    }
}
