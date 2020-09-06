package com.minetec.backend.dto.filter;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VehicleFilterForm {

    @NotNull
    private String fleetNo = "";
    @NotNull
    private SiteFilterForm siteFilterForm = new SiteFilterForm();
    @NotNull
    private VehicleTypeFilterForm vehicleTypeFilterForm = new VehicleTypeFilterForm();
    @NotNull
    private String vinNo = "";
    @NotNull
    private String serialNo = "";
    @NotNull
    private String unit = "";
    @NotNull
    private String registrationNo = "";
    @NotNull
    private boolean isUsable = true;
    @NotNull
    private String currentMachineHours = "";
    @NotNull
    private String serviceIntervalHours = "";
    @NotNull
    private String lastServiceDate = "";
    @NotNull
    private String lastServiceHours = "";

    private int page;
    private int size;
}
