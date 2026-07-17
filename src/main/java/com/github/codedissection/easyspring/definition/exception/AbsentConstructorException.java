package com.github.codedissection.easyspring.definition.exception;

public class AbsentConstructorException extends BeanDefinitionException{
    public AbsentConstructorException(String message) {
        super(message);
    }

    public AbsentConstructorException(String message, Throwable cause) {
        super(message, cause);
    }
}
