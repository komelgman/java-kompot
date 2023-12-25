package com.github.komelgman.kompot.mixin4j;

import com.github.komelgman.kompot.mixin4j.exception.MixinNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
class MixedObjectMachinetTest {
    private MixedObject mixedObject;

    @BeforeEach
    void setUp() {
        mixedObject = new MixedObject();
    }

    @Test
    void testMixWith() {
        // Mix the implementation with the descriptor
        mixedObject.mixWith(Descriptor.class, new Implementation());

        // Verify that the implementation is mixed with the descriptor
        Assertions.assertTrue(mixedObject.is(Descriptor.class));
        Assertions.assertTrue(mixedObject.as(Descriptor.class) instanceof Implementation);
    }

    @Test
    void testUnmix() {
        // Mix the implementation with the descriptor
        mixedObject.mixWith(Descriptor.class, new Implementation());

        // Unmix the implementation from the descriptor
        Descriptor implementation = mixedObject.unmix(Descriptor.class);

        // Verify that the implementation is un-mixed from the descriptor
        Assertions.assertThrows(MixinNotFoundException.class, () -> mixedObject.as(Descriptor.class));
        Assertions.assertNotNull(implementation);
    }

    @Test
    void testRemix() {
        // Mix the implementation with the descriptor
        mixedObject.mixWith(Descriptor.class, new Implementation());

        // Create a new implementation
        class NewImplementation extends Descriptor {}

        // Remix the descriptor with the new implementation
        mixedObject.remix(Descriptor.class, new NewImplementation());

        // Verify that the new implementation is mixed with the descriptor
        Assertions.assertTrue(mixedObject.is(Descriptor.class));
        Assertions.assertTrue(mixedObject.as(Descriptor.class) instanceof NewImplementation);
    }

    @Test
    void testAs() {
        // Mix the implementation with the descriptor
        mixedObject.mixWith(Descriptor.class, new Implementation());

        // Verify that the implementation can be retrieved as the descriptor
        Descriptor implementation = mixedObject.as(Descriptor.class);
        Assertions.assertNotNull(implementation);
        Assertions.assertTrue(mixedObject.as(Descriptor.class) instanceof Implementation);
    }

    @Test
    void testAsWithProcessor() {
        // Mix the implementation with the descriptor
        mixedObject.mixWith(Descriptor.class, new Implementation());

        // Define a processor function
        Function<Descriptor, String> processor = (d) -> "Processed";

        // Verify that the processor function is applied to the implementation
        String result = mixedObject.as(Descriptor.class, processor);
        Assertions.assertEquals("Processed", result);
    }

    @Test
    void testIfIs(@Mock Consumer<Descriptor> consumer) {
        // Mix the implementation with the descriptor
        mixedObject.mixWith(Descriptor.class, new Implementation());

        // Verify that the consumer function is applied to the implementation
        mixedObject.ifIs(Descriptor.class, consumer);

        // Verify that the consumer function is called
        Mockito.verify(consumer).accept(Mockito.any(Descriptor.class));
    }

    @Test
    void testIs() {
        // Mix the implementation with the descriptor
        mixedObject.mixWith(Descriptor.class, new Implementation());

        // Verify that the implementation is of the descriptor type
        Assertions.assertTrue(mixedObject.is(Descriptor.class));
    }

    @Test
    void testMixins() {
        // Mix the implementation with the descriptor
        mixedObject.mixWith(Descriptor.class, new Implementation());

        // Verify that the descriptor is included in the set of mixins
        Assertions.assertTrue(mixedObject.mixins().contains(Descriptor.class));
    }

    static class Descriptor {}
    static class Implementation extends Descriptor {}
}
