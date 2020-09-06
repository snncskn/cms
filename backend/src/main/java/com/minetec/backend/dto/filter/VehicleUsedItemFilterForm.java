package com.minetec.backend.dto.filter;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class VehicleUsedItemFilterForm {

    private String startDate = "";
    private String endDate = "";

    @NotNull
    private UUID vehicleUuid;

    private int page;
    private int size;
}
