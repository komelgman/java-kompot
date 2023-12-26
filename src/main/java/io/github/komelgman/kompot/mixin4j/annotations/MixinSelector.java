package io.github.komelgman.kompot.mixin4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MixinSelector {
    Class<?> mixin();
    Class<?>[] onlyFor() default {};
    Class<?>[] except() default {};
}
