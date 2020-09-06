package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class ItemAttributeListCreateForm {


    private UUID vehicleTypeUuid;

    @NotNull
    private UUID vehicleUuid;

    @NotNull
    private List<ItemAttributeListDetailCreateForm> vehicleAttributeListDetailCreateForm;
}
