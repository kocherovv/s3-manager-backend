package net.example.exception;

import net.example.model.AppStatusCode;

public class AppException extends RuntimeException {

    public AppException(AppStatusCode errorCode) {
        super(errorCode.getMessage());
    }

    public AppException(AppStatusCode errorCode, Throwable e) {
        super(errorCode.getMessage() + " : " + e.getMessage());
    }
}
