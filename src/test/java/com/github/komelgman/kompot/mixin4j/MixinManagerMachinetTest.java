package com.github.komelgman.kompot.mixin4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
class MixinManagerMachinetTest {
    private MixinManager mixinManager;

    @BeforeEach
    void setUp() {
        mixinManager = new MixinManager();
    }

    @Test
    void testMixWith() {
        // Mix the implementation with the descriptor
        mixinManager.mixWith(Descriptor.class, new Implementation());

        // Verify that the implementation is mixed with the descriptor
        Assertions.assertTrue(mixinManager.is(Descriptor.class));
    }

    @Test
    void testUnmix() {
        // Mix the implementation with the descriptor
        mixinManager.mixWith(Descriptor.class, new Implementation());

        // Unmix the implementation from the descriptor
        mixinManager.unmix(Descriptor.class);

        // Verify that the implementation is no longer mixed with the descriptor
        Assertions.assertFalse(mixinManager.is(Descriptor.class));
    }

    @Test
    void testRemix() {
        // Mix the implementation with the descriptor
        mixinManager.mixWith(Descriptor.class, new Implementation());

        // Create a new implementation
        class NewImplementation extends Descriptor {}

        // Remix the descriptor with the new implementation
        mixinManager.remix(Descriptor.class, new NewImplementation());

        // Verify that the new implementation is mixed with the descriptor
        Assertions.assertTrue(mixinManager.is(Descriptor.class));
        Assertions.assertTrue(mixinManager.as(Descriptor.class) instanceof NewImplementation);
    }

    @Test
    void testAs() {
        // Mix the implementation with the descriptor
        mixinManager.mixWith(Descriptor.class, new Implementation());

        // Get the implementation as the descriptor
        Descriptor result = mixinManager.as(Descriptor.class);

        // Verify that the result is the implementation
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result instanceof Implementation);
    }

    @Test
    void testAsWithProcessor() {
        // Mix the implementation with the descriptor
        mixinManager.mixWith(Descriptor.class, new Implementation());

        // Define a processor function
        Function<Descriptor, String> processor = (d) -> "Processed";

        // Get the processed result of the implementation as the descriptor
        String result = mixinManager.as(Descriptor.class, processor);

        // Verify that the result is the processed value
        Assertions.assertEquals("Processed", result);
    }

    @Test
    void testIfIs(@Mock Consumer<Descriptor> consumer) {
        // Mix the implementation with the descriptor
        mixinManager.mixWith(Descriptor.class, new Implementation());

        // Verify that the consumer function is applied to the implementation
        mixinManager.ifIs(Descriptor.class, consumer);

        // Verify that the consumer function is called
        Mockito.verify(consumer).accept(Mockito.any(Descriptor.class));
    }

    @Test
    void testIs() {
        // Mix the implementation with the descriptor
        mixinManager.mixWith(Descriptor.class, new Implementation());

        // Verify that the implementation is mixed with the descriptor
        Assertions.assertTrue(mixinManager.is(Descriptor.class));
        Assertions.assertTrue(mixinManager.as(Descriptor.class) instanceof Implementation);
    }

    @Test
    void testMixins() {
        // Mix the implementation with the descriptor
        mixinManager.mixWith(Descriptor.class, new Implementation());

        // Get the set of mixins
        Set<Class<?>> mixins = mixinManager.mixins();

        // Verify that the set contains the descriptor class
        Assertions.assertTrue(mixins.contains(Descriptor.class));
    }

    // Create a descriptor and implementation
    static class Descriptor {}
    static class Implementation extends Descriptor {}
}
