package com.minetec.backend.dto.form;

import com.minetec.backend.dto.info.VehicleTypeAttributeValueInfo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * @author mozcan
 */
@Data
public class VehicleAttributeListCreateUpdateForm {

    @NotNull
    private UUID vehicleUuid;

    private String vehicleTypeAttributeDesc;

    private List<VehicleTypeAttributeValueInfo> vehicleTypeAttributeValues;

}
