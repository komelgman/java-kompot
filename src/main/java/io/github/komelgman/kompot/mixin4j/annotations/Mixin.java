package io.github.komelgman.kompot.mixin4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Mixin {
    Class<?>[] owners();
    MixinPrecondition precondition();
    int order() default Integer.MAX_VALUE;
}
