package com.github.codedissection.easyspring.context.exception;

public abstract class ContextException extends RuntimeException {

    public ContextException(String message) {
        super(message);
    }

    public ContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
