package com.github.codedissection.easyspring.definition.exception;

public abstract class BeanDefinitionException extends RuntimeException {

    public BeanDefinitionException(String message) {
        super(message);
    }

    public BeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
