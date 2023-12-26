package io.github.komelgman.kompot.misc.ables;

import java.util.function.Supplier;

@FunctionalInterface
public interface Supportable extends Supplier<Boolean> {
    boolean isSupported();

    default boolean isNotSupported() {
        return !isSupported();
    }

    @Override
    default Boolean get() {
        return isSupported();
    }
}
