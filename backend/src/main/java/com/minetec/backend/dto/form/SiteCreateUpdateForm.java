package com.minetec.backend.dto.form;

import lombok.Data;

import java.util.UUID;

@Data
public class SiteCreateUpdateForm {
    private UUID uuid;
    private String description;
    private UUID supplierUuid;
}
