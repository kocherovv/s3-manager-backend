package net.example.exception;

import net.example.model.AppStatusCode;

public class NotFoundException extends RuntimeException {

    public NotFoundException(AppStatusCode errorCode) {
        super(errorCode.getMessage());
    }

    public NotFoundException() {
        super(AppStatusCode.NOT_FOUND_EXCEPTION.getMessage());
    }
}
