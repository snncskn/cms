package com.minetec.backend.dto.form;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageCreateUpdateForm {
    private UUID uuid;
    private UUID orderItemUuid;
    private String message;
}
