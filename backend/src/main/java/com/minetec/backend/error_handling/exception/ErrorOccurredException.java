package com.minetec.backend.error_handling.exception;

public class ErrorOccurredException extends BaseException {

    public ErrorOccurredException(final String code, final String message) {
        super(code, message);
    }

    public ErrorOccurredException(final String code, final String message, final Exception ex) {
        super(code, message, ex);
    }
}
