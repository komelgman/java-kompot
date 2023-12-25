package com.github.komelgman.kompot.mixin4j;

public interface Mixable {
    <D, I extends D> void mixWith(Class<D> descriptor, I implementation);
    <T> T unmix(Class<T> descriptor);
    <T> void remix(Class<T> descriptor, T implementation);
}
