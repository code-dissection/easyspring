package com.github.codedissection.easyspring.scanner.exception;

public class ExternalLibraryException extends RuntimeException {
    public ExternalLibraryException(String message) {
        super(message);
    }

    public ExternalLibraryException(String message, Throwable cause) {
        super(message, cause);
    }
}
