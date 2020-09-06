package com.minetec.backend.error_handling.exception;

public class TokenExpireException extends BaseException {

    public TokenExpireException(final String code, final String message, final Exception ex) {
        super(code, message, ex);
    }

    public TokenExpireException(final String code, final String message) {
        super(code, message);
    }
}
