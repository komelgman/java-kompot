package com.github.komelgman.kompot.mixin4j.exception;

public class MixinNotFoundException extends IllegalStateException {
    public MixinNotFoundException(Object owner, Class<?> descriptor) {
        super(mixinNotFoundExceptionMessage(owner, descriptor));
    }

    public  static <T> String mixinNotFoundExceptionMessage(Object owner, Class<T> descriptor) {
        return String.format(
                "Mixin %s is not registered in %s",
                descriptor.getSimpleName(),
                owner.getClass().getSimpleName()
        );
    }
}
