package com.github.codedissection.easyspring.definition.exception;

public class MultipleImplementationException extends BeanDefinitionException {

    public MultipleImplementationException(String message) {
        super(message);
    }

    public MultipleImplementationException(String message, Throwable cause) {
        super(message, cause);
    }
}
