package com.minetec.backend.dto.info;

import lombok.Data;

import java.util.UUID;

@Data
public class MessageInfo {
    private UUID uuid;
    private UUID orderItemUuid;
    private String message;
    private String createDate;
    private String fullName;
}
