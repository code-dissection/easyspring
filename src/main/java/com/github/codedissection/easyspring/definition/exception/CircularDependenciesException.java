package com.github.codedissection.easyspring.definition.exception;

public class CircularDependenciesException extends BeanDefinitionException {

    public CircularDependenciesException(String message) {
        super(message);
    }

    public CircularDependenciesException(String message, Throwable cause) {
        super(message, cause);
    }
}
