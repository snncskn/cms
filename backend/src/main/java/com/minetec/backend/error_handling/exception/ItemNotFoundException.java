package com.minetec.backend.error_handling.exception;

public class ItemNotFoundException extends BaseException {

    public ItemNotFoundException(final String code, final String message) {
        super(code, message);
    }
}
