package com.github.komelgman.kompot.download4j;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
public class Download4J {
    private final ExecutorService executor;

    public Download4J() {
        this(ForkJoinPool.commonPool());
    }

    public <R> CompletableFuture<R> download(DownloadTask<R> task, DownloadListener<R> listener) {
        var ref = new AtomicReference<CompletableFuture<R>>();
        var retry = new RetryDownload<>(ref, new DownloadWorker<>(task, listener));

        ref.set(CompletableFuture.supplyAsync(retry, executor));

        return ref.get();
    }
}
