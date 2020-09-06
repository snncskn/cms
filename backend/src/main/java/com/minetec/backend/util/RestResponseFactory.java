package com.minetec.backend.util;

import com.minetec.backend.dto.RestResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public final class RestResponseFactory {

    private final MessageSource messageSource;

    public RestResponseFactory(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public RestResponse error(final String code, final String msgId) {
        return createMessage("ERROR", code, msgId, null);
    }

    public RestResponse error(final String code, final String msgId, final Object data) {
        return createMessage("ERROR", code, msgId, data);
    }

    public RestResponse info(final String code, final String msgId) {
        return createMessage("INFO", code, msgId, null);
    }

    public RestResponse info(final String code, final String msgId, final Object data) {
        return createMessage("INFO", code, msgId, data);
    }

    public RestResponse success(final String code, final String msgId) {
        return createMessage("SUCCESS", code, msgId, null);
    }

    public RestResponse success(final String code, final String msgId, final Object data) {
        return createMessage("SUCCESS", code, msgId, data);
    }

    public RestResponse warning(final String code, final String msgId) {
        return createMessage("WARNING", code, msgId, null);
    }

    public RestResponse warning(final String code, final String msgId, final Object data) {
        return createMessage("WARNING", code, msgId, data);
    }

    private <T> RestResponse createMessage(final String type, final String code, final String msgId, final T data) {
        final RestResponse<T> restResponse = new RestResponse<>();
        restResponse.setType(type);
        restResponse.setCode(code);
        restResponse.setMsgId(msgId);
        restResponse.setText(messageSource.getMessage(msgId, null, msgId, Locale.getDefault()));
        restResponse.setData(data);

        return restResponse;
    }
}
