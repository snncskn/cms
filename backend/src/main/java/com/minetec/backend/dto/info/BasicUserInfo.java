package com.minetec.backend.dto.info;

import lombok.Data;

import java.util.UUID;


@Data
public class BasicUserInfo {
    private UUID uuid;
    private String email;
    private String fullName;
    private String position;
    private String createDate;
    private String staffNumber;
}
