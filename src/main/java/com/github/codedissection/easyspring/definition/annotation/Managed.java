package com.github.codedissection.easyspring.definition.annotation;

import com.github.codedissection.easyspring.definition.annotation.root.EasySpringAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@EasySpringAnnotation
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Managed {
}
