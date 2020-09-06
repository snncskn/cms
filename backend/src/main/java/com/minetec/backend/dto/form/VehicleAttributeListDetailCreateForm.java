package com.minetec.backend.dto.form;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class VehicleAttributeListDetailCreateForm {

    @NotNull
    private UUID vehicleAttributeUuid;

    @NotNull
    private UUID vehicleAttributeValueUuid;

}
