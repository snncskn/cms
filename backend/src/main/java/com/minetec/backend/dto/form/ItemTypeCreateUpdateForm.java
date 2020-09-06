package com.minetec.backend.dto.form;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ItemTypeCreateUpdateForm {
    private String vehicleTypeDesc;
    private List<ItemTypeAttributeCreateUpdateForm> vehicleTypeAttributeCreateUpdateForms;
    private UUID uuid;
}
