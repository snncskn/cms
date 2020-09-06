package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class VehicleTypeInfo {

    @NotNull
    private UUID uuid;
    private String name;
    private List<VehicleTypeAttributeInfo> vehicleTypeAttributes;

}
