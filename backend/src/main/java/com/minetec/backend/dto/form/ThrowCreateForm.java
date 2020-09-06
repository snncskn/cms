package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ThrowCreateForm {

    @NotEmpty
    @NotNull
    private String data;
}
