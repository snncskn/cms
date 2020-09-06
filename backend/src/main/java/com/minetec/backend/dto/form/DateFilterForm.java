package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class DateFilterForm {
    @NotNull
    @NotEmpty
    private String startDate;

    @NotNull
    @NotEmpty
    private String endDate;

    @NotNull
    @NotEmpty
    private String status;

}
