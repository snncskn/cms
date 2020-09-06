package com.minetec.backend.dto.filter.warehouse;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TransferFilterForm {
    @NotNull
    private String startDate = "";
    @NotNull
    private String endDate = "";
    @NotNull
    private String status = "";
    @NotNull
    private String transferNumber = "";
    @NotNull
    private String sourceSite = "";
    @NotNull
    private String targetSite = "";
    private int page;
    private int size;
}
