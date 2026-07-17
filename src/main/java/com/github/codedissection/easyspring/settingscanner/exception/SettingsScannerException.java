package com.github.codedissection.easyspring.settingscanner.exception;

public abstract class SettingsScannerException extends RuntimeException{
    public SettingsScannerException(String message) {
        super(message);
    }

    public SettingsScannerException(String message, Throwable cause) {
        super(message, cause);
    }
}
