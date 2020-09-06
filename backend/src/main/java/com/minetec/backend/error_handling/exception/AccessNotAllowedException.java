package com.minetec.backend.error_handling.exception;

public class AccessNotAllowedException extends BaseException {

    public AccessNotAllowedException(final String code, final String message) {
        super(code, message);
    }

}
