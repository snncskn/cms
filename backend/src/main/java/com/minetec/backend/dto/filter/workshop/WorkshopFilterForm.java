package com.minetec.backend.dto.filter.workshop;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class WorkshopFilterForm {
    @NotNull
    private String breakDownStartDate = "";

    @NotNull
    private @NotEmpty String breakDownEndDate = "";

    @NotNull
    private String startDate = "";

    @NotNull
    private String endDate = "";

    private String filter = "";

    private UUID vehicleUuid;

    @NotNull
    private String fleetNumber = "";
    @NotNull
    private String reportNumber = "";
    @NotNull
    private String status = "";
    @NotNull
    private String site = "";

    private int page;
    private int size;

}
