package io.github.komelgman.kompot.mixin4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MixinManagerTest {
    public static final String VALUE_1 = "value1";
    private MixinManager mixinManager;

    // tests

    @BeforeEach
    void setUpEach() {
        mixinManager = new MixinManager();
    }

    @Test
    void testRegisterMixin() {
        final SampleMixinImpl mixin = new SampleMixinImpl(VALUE_1);
        mixinManager.mixWith(SampleMixin.class, mixin);

        assertAll(
                () -> mixinManager.is(SampleMixin.class),
                () -> assertSame(mixin, mixinManager.as(SampleMixin.class)),
                () -> assertEquals(1, mixinManager.mixins().size()),
                () -> assertEquals(VALUE_1, mixinManager.as(SampleMixin.class).getProperty1()),
                () -> assertEquals(VALUE_1, mixinManager.as(SampleMixin.class, SampleMixin::getProperty1))
        );
    }

    @Test
    void testRegisterMixinTwice() {
        final SampleMixinImpl mixin = new SampleMixinImpl(VALUE_1);
        mixinManager.mixWith(SampleMixin.class, mixin);

        assertThrows(IllegalStateException.class, () -> mixinManager.mixWith(SampleMixin.class, mixin));
    }

    @Test
    void testUnregisterMixin() {
        final SampleMixinImpl mixin = new SampleMixinImpl(VALUE_1);
        mixinManager.mixWith(SampleMixin.class, mixin);

        assertEquals(mixin, mixinManager.unmix(SampleMixin.class));

        assertAll(
                () -> assertFalse(mixinManager.is(SampleMixin.class)),
                () -> assertNull(mixinManager.as(SampleMixin.class)),
                () -> assertThrows(IllegalStateException.class,
                        () -> mixinManager.as(SampleMixin.class, SampleMixin::getProperty1))
        );
    }

    @Test
    void testMixinIfIs(@Mock Consumer<SampleMixin> processor) {
        final SampleMixinImpl mixin = new SampleMixinImpl(VALUE_1);
        mixinManager.mixWith(SampleMixin.class, mixin);

        mixinManager.ifIs(SampleMixin.class, processor);

        Mockito.verify(processor, times(1)).accept(mixin);
    }


    interface SampleMixin {
        String getProperty1();
    }

    static class SampleMixinImpl implements SampleMixin {
        private final String property1;

        SampleMixinImpl(String property1) {
            this.property1 = property1;
        }

        @Override
        public String getProperty1() {
            return property1;
        }
    }
}
