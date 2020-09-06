package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class VehicleAttributeValueInfo {
    @NotNull
    private UUID uuid;
    private String vehicleAttributeValue;
}
