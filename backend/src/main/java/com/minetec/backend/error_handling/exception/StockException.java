package com.minetec.backend.error_handling.exception;

public class StockException extends BaseException {

    public StockException(final String code, final String message) {
        super(code, message);
    }

    public StockException(final String code, final String message, final Exception ex) {
        super(code, message, ex);
    }
}
