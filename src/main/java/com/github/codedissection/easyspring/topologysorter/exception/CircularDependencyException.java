package com.github.codedissection.easyspring.topologysorter.exception;

public class CircularDependencyException extends GraphInitializerException {

    public CircularDependencyException(String message) {
        super(message);
    }
}
