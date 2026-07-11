package com.github.codedissection.easyspring.scanner.exception;

public class MultipleConstructorsException extends ProjectScannerException {
    public MultipleConstructorsException(String message) {
        super(message);
    }

    public MultipleConstructorsException(String message, Throwable cause) {
        super(message, cause);
    }
}
