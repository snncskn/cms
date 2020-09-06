package com.minetec.backend.dto.filter.workshop;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RequestListFilterForm {
    @NotNull
    private String requestStartDate = "";
    @NotNull
    private String requestEndDate = "";

    private String filter = "";

    private int page;
    private int size;

    @NotNull
    private String jobCardNumber = "";
    @NotNull
    private String fleetNumber = "";
    @NotNull
    private String requestUser = "";
    @NotNull
    private String stockCode = "";
    @NotNull
    private String itemDescription = "";
    @NotNull
    private String status = "";

}
