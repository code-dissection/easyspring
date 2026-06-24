package com.github.codedissection.easyspring.definition.exception;

public class BeanDefinitionCreateException extends BeanDefinitionException {

    public BeanDefinitionCreateException(String message) {
        super(message);
    }

    public BeanDefinitionCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
