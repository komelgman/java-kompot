package com.github.komelgman.kompot.mixin4j.annotations;

import com.github.komelgman.kompot.misc.ables.Supportable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MixinPrecondition {
    Class<Supportable> value();
}
