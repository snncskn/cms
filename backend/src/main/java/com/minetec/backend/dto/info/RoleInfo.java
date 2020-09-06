package com.minetec.backend.dto.info;

import lombok.Data;

import java.util.UUID;

@Data
public class RoleInfo {
    private UUID uuid;
    private String name;
}
