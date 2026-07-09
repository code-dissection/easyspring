package com.github.codedissection.easyspring.projectscanner.exception;

public abstract class ProjectScannerException extends RuntimeException {

    public ProjectScannerException(String message) {
        super(message);
    }

    public ProjectScannerException(String message, Throwable cause) {
        super(message, cause);
    }
}
