package com.github.codedissection.easyspring.context.exception;

public class ShutdownProjectException extends ContextException {

    public ShutdownProjectException(String message) {
        super(message);
    }

    public ShutdownProjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
