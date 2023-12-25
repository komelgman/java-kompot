package com.github.komelgman.kompot.mixin4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MixedObjectTest {
    public static final String INITIAL_VALUE = "initial value";

    @Test
    void testRegisterMixin() {
        final MixedObject mixedObject = new SampleMixedObject();

        assertAll(
                () -> mixedObject.is(SampleMixin.class),
                () -> assertEquals(1, mixedObject.mixins().size()),
                () -> assertEquals(INITIAL_VALUE, mixedObject.as(SampleMixin.class).getProperty1()),
                () -> assertEquals(INITIAL_VALUE, mixedObject.as(SampleMixin.class, SampleMixin::getProperty1))
        );
    }

    @Test
    void testUnregisterMixin() {
        final SampleMixedObject mixedObject = new SampleMixedObject();
        mixedObject.clean();

        assertAll(
                () -> assertFalse(mixedObject.is(SampleMixin.class)),
                () -> assertThrows(IllegalStateException.class, () -> mixedObject.as(SampleMixin.class)),
                () -> assertThrows(IllegalStateException.class, () ->
                        mixedObject.as(SampleMixin.class, SampleMixin::getProperty1))
        );
    }

    @Test
    void testUpdateMixin() {
        final SampleMixedObject mixedObject = new SampleMixedObject();
        final String newValue = "new value";

        mixedObject.update(newValue);

        assertAll(
                () -> mixedObject.is(SampleMixin.class),
                () -> assertEquals(1, mixedObject.mixins().size()),
                () -> assertEquals(newValue, mixedObject.as(SampleMixin.class).getProperty1()),
                () -> assertEquals(newValue, mixedObject.as(SampleMixin.class, SampleMixin::getProperty1))
        );
    }

    @Test
    void testMixinIfIs(@Mock Consumer<SampleMixin> processor) {
        final MixedObject mixedObject = new SampleMixedObject();
        final SampleMixin mixin = mixedObject.as(SampleMixin.class);

        mixedObject.ifIs(SampleMixin.class, processor);

        Mockito.verify(processor, times(1)).accept(mixin);
    }

    static class SampleMixedObject extends MixedObject {
        protected SampleMixedObject() {
            mixWith(SampleMixin.class, new SampleMixinImpl(INITIAL_VALUE));
        }

        public void update(String value) {
            remix(SampleMixin.class, new SampleMixinImpl(value));
        }

        public void clean() {
            unmix(SampleMixin.class);
        }
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
