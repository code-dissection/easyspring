package com.github.codedissection.easyspring.bean.exception;

public abstract class BeanException extends RuntimeException {

    public BeanException(String message) {
        super(message);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
