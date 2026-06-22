package com.github.codedissection.easyspring.definition.exception;

public class CircularDependenciesException extends RuntimeException {
    public CircularDependenciesException(String message) {
        super(message);
    }

    public CircularDependenciesException(String message, Throwable cause) {
        super(message, cause);
    }
}
