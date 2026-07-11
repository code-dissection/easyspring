package com.github.codedissection.easyspring.scanner.exception;

public class TypeInvariantViolationException extends ProjectScannerException{
    public TypeInvariantViolationException(String message) {
        super(message);
    }

    public TypeInvariantViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
