package io.github.komelgman.kompot.mixin4j;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Mixed {
    <T> T as(Class<T> descriptor);
    <T, R> R as(Class<T> descriptor, Function<T, R> processor);
    <T> void ifIs(Class<T> descriptor, Consumer<T> processor);
    <T> boolean is(Class<T> descriptor);
    Set<Class<?>> mixins();
}
