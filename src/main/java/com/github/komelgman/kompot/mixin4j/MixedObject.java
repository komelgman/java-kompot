package com.github.komelgman.kompot.mixin4j;

import com.github.komelgman.kompot.mixin4j.exception.MixinNotFoundException;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MixedObject implements Mixed {
    private final MixinManager mixinManager;

    protected MixedObject() {
        mixinManager = new MixinManager();
    }

    protected <D, I extends D> void mixWith(Class<D> descriptor, I implementation) {
        if (implementation != null) {
            mixinManager.mixWith(descriptor, implementation);
        }
    }

    @SuppressWarnings("unused")
    protected <T> T unmix(Class<T> descriptor) {
        return mixinManager.unmix(descriptor);
    }

    protected <T> void remix(Class<T> descriptor, T newImplementation) {
        mixinManager.remix(descriptor, newImplementation);
    }

    @Override
    public <T> T as(Class<T> descriptor) {
        T instance = mixinManager.as(descriptor);
        if (instance == null) {
            throw new MixinNotFoundException(this, descriptor);
        }

        return instance;
    }

    @Override
    public <T, R> R as(Class<T> descriptor, Function<T, R> processor) {
        try {
            return mixinManager.as(descriptor, processor);
        } catch (MixinNotFoundException e) {
            MixinNotFoundException mixinNotFoundException = new MixinNotFoundException(this, descriptor);
            mixinNotFoundException.initCause(e);

            throw mixinNotFoundException;
        }
    }

    @Override
    public <T> void ifIs(Class<T> descriptor, Consumer<T> processor) {
        mixinManager.ifIs(descriptor, processor);
    }

    @Override
    public <T> boolean is(Class<T> descriptor) {
        return mixinManager.is(descriptor);
    }

    @Override
    public Set<Class<?>> mixins() {
        return mixinManager.mixins();
    }
}
