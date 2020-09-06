package com.minetec.backend.dto;


import lombok.Data;


@Data
public final class RestResponse<T> {

    public static final String INVALID_CREDENTIALS = "Header is null";

    private T data;
    private String msgId;
    private String code;
    private String text;
    private String type;
}

