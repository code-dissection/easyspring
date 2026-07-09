package com.github.codedissection.easyspring.topologysorter.exception;

public abstract class GraphInitializerException extends RuntimeException {

    public GraphInitializerException(String message) {
        super(message);
    }

    public GraphInitializerException(String message, Throwable cause) {
        super(message, cause);
    }
}
