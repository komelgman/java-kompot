package com.github.komelgman.kompot.vfs;


import com.github.komelgman.kompot.misc.Processor;
import com.github.komelgman.kompot.streams.StreamHelper;
import com.github.komelgman.kompot.streams.StreamProvider;

public interface FileStorage<RootType> {
    <R> R applyProcessor(Processor<PathResolver, R> processor);

    default <R> R processFile(String filePath, Processor<StreamProvider, R> processor) {
        return applyProcessor(fs -> processor.process(StreamHelper.newStreamProvider(filePath, fs)));
    }

    RootType getRoot();
}
