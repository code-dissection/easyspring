package com.github.codedissection.easyspring.definition.exception;

public class ScopedTargetProblemException extends BeanDefinitionException{
    public ScopedTargetProblemException(String message) {
        super(message);
    }

    public ScopedTargetProblemException(String message, Throwable cause) {
        super(message, cause);
    }
}
