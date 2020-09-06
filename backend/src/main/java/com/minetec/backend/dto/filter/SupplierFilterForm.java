package com.minetec.backend.dto.filter;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SupplierFilterForm {

    @NotNull
    private String value = "";
    private int page;
    private int size;
}
