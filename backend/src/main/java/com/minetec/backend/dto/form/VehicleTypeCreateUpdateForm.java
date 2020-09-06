package com.minetec.backend.dto.form;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class VehicleTypeCreateUpdateForm {
    private String vehicleTypeDesc;
    private List<VehicleTypeAttributeCreateUpdateForm> vehicleTypeAttributeCreateUpdateForms;
    private UUID uuid;
}
