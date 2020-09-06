package com.minetec.backend.util;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @Sinan
 */

@Data
public class Response<T> extends org.springframework.http.ResponseEntity<T> {
    public Response(final T body, final HttpStatus status) {
        super(body, status);
    }
}
