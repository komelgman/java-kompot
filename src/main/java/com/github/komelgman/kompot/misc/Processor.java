package com.github.komelgman.kompot.misc;

@FunctionalInterface
public interface Processor<T, R> {
    R process(T argument) throws Exception;
}
