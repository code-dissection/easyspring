package com.github.codedissection.easyspring.definition.exception;

public class MissingImplementationException extends BeanDefinitionException {

    public MissingImplementationException(String message) {
        super(message);
    }

    public MissingImplementationException(String message, Throwable cause) {
        super(message, cause);
    }
}
