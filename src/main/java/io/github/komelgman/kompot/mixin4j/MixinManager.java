package io.github.komelgman.kompot.mixin4j;

import io.github.komelgman.kompot.misc.ables.Supportable;
import io.github.komelgman.kompot.mixin4j.exception.MixinNotFoundException;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class MixinManager implements Mixable, Mixed {
    private final Map<Class<?>, Object> mixins;

    public MixinManager() {
        this.mixins = new LinkedHashMap<>();
    }

    @Override
    public <D, I extends D> void mixWith(Class<D> descriptor, I implementation) {
        if (is(descriptor)) {
            throw new IllegalStateException(String.format(
                    "Mixin %s already registered in %s",
                    descriptor.getSimpleName(),
                    this.getClass().getSimpleName()
            ));
        }

        mixins.put(descriptor, implementation);
    }

    @Override
    public <T> T unmix(Class<T> descriptor) {
        //noinspection unchecked
        return (T) mixins.remove(descriptor);
    }

    @Override
    public <T> void remix(Class<T> descriptor, T implementation) {
        if (is(descriptor)) {
            unmix(descriptor);
        }

        mixWith(descriptor, implementation);
    }

    @Override
    public <T> T as(Class<T> descriptor) {
        //noinspection unchecked
        final T mixin = (T) mixins.get(descriptor);
        if (mixin == null && log.isDebugEnabled()) {
            log.debug(MixinNotFoundException.mixinNotFoundExceptionMessage(this, descriptor));
        }

        return mixin;
    }

    @Override
    public <T, R> R as(Class<T> descriptor, Function<T, R> processor) {
        T mixin = as(descriptor);
        if (mixin == null) {
            throw new MixinNotFoundException(this, descriptor);
        }

        return processor.apply(mixin);
    }

    @Override
    public <T> void ifIs(Class<T> descriptor, Consumer<T> processor) {
        if (is(descriptor)) {
            processor.accept(as(descriptor));
        }
    }

    @Override
    public <T> boolean is(Class<T> descriptor) {
        return mixins.containsKey(descriptor) && isSupported(descriptor);
    }

    private <T> boolean isSupported(Class<T> descriptor) {
        return !(mixins.get(descriptor) instanceof Supportable supportable) || supportable.isSupported();
    }

    @Override
    public Set<Class<?>> mixins() {
        return mixins.keySet();
    }
}
