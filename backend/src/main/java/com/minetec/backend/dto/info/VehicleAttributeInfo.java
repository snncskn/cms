package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class VehicleAttributeInfo {

    @NotNull
    private UUID uuid;
    private String name;
    private List<VehicleAttributeValueInfo> vehicleAttributeValues = new ArrayList<>();
}
