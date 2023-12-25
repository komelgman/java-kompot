package com.github.komelgman.kompot.vfs;

import com.github.komelgman.kompot.misc.Processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirBasedFileStorage extends AbstractFileStorage<Path> {

    private final DirPathResolver pathResolver;

    public DirBasedFileStorage(Path root) {
        super(root);

        if (!root.toFile().exists()) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        String.format("Root path [%s] doesn't exists and can't be created",
                                getRoot()), e
                );
            }
        }

        pathResolver = new DirPathResolver(root);
    }

    public <R> R applyProcessor(Processor<PathResolver, R> processor) {
        try {
            return processor.process(pathResolver);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
