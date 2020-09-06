package com.minetec.backend.error_handling.exception;

/**
 * @author Sinan
 */
public class FrontEndException extends BaseException {
    public FrontEndException(final String code, final String message) {
        super(code, message);
    }

}
