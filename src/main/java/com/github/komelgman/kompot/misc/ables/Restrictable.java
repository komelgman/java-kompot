package com.github.komelgman.kompot.misc.ables;

import java.util.function.Supplier;

@FunctionalInterface
public interface Restrictable extends Supplier<Boolean> {
    boolean isRestricted();

    default boolean isNotRestricted() {
        return !isRestricted();
    }

    @Override
    default Boolean get() {
        return isRestricted();
    }
}
