package com.minetec.backend.error_handling.exception;

public class BaseException extends RuntimeException {

    private String code;

    BaseException(final String code, final String message) {

        super(message);

        this.code = code;
    }

    BaseException(final String code, final String message, final Exception ex) {

        super(message, ex);

        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
