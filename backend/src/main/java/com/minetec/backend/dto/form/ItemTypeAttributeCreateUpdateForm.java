package com.minetec.backend.dto.form;

import lombok.Data;

import java.util.UUID;

@Data
public class ItemTypeAttributeCreateUpdateForm {

    private UUID uuid;

    private String name;

    private UUID vehicleAttributeUuid;

    private UUID vehicleTypeUuid;

}
