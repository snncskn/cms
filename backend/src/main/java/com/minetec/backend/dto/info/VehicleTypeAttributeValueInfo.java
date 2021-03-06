package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author mozcan
 */
@Data
public class VehicleTypeAttributeValueInfo {

    @NotNull
    private UUID uuid;

    private String vehicleTypeAttributeValueDesc;

}
