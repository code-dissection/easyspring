package com.github.codedissection.easyspring.definition.exception;

public class MultipleImplementationException extends RuntimeException{
    public MultipleImplementationException(String message) {
        super(message);
    }

    public MultipleImplementationException(String message, Throwable cause) {
        super(message, cause);
    }
}
