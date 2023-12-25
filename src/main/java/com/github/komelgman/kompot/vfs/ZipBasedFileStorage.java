package com.github.komelgman.kompot.vfs;

import com.github.komelgman.kompot.misc.Processor;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


public class ZipBasedFileStorage extends AbstractFileStorage<Path> {

    public ZipBasedFileStorage(File zipPackage) {
        super(zipPackage.toPath());

        if (!zipPackage.exists()) {
            throw new IllegalStateException(
                    String.format("Root path [%s] doesn't exists", getRoot()));
        }
    }

    public <R> R applyProcessor(Processor<PathResolver, R> processor) {
        Map<String, Object> env = new HashMap<>();
        env.put("create", !Files.exists(getRoot()));

        try (FileSystem fs = ZipHelper.getZipFSProvider().newFileSystem(getRoot(), env)) {
            ZipPathResolver vfs = new ZipPathResolver(fs);
            return processor.process(vfs);
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
