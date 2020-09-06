package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class VehicleAttributeValueCreateUpdateForm {

    @NotNull
    @NotEmpty
    private String desc;

    private UUID vehicleAttributeUuid;

    private UUID attributeValueUuid;


}
