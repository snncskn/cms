package com.minetec.backend.dto.filter;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OrderFilterForm {
    @NotNull
    private String startDate = "";

    @NotNull
    private String endDate = "";

    @NotNull
    private String status = "";

    @NotNull
    private String supplierName = "";

    @NotNull
    private String requestNumber = "";

    @NotNull
    private String invoiceNumber = "";

    @NotNull
    private String orderNumber = "";

    private int page;
    private int size;
}
