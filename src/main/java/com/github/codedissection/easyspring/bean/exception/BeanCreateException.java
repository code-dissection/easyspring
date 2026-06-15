package com.github.codedissection.easyspring.bean.exception;

public class BeanCreateException extends RuntimeException {

    public BeanCreateException(String message) {
        super(message);
    }

    public BeanCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
