package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class ItemTypeAttributeInfo {

    @NotNull
    private UUID uuid;
    private String vehicleAttributeName;
    private List<ItemAttributeValueInfo> vehicleAttributeValues;
    private UUID selectedVehicleAttrUuid;


}
