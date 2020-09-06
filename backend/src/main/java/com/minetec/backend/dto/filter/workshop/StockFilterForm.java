package com.minetec.backend.dto.filter.workshop;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StockFilterForm {
    @NotNull
    private String startDate;
    @NotNull
    private String endDate;
    @NotNull
    private String createdDate;
    @NotNull
    private String stockCode;
    @NotNull
    private String itemDescription;
    @NotNull
    private String siteName;
    @NotNull
    private String moveType;
    @NotNull
    private String quantity;
    @NotNull
    private String moveNumber;

    private int page;
    private int size;

}
