package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class SupplierCreateUpdateForm {
    private UUID uuid;
    @NotNull
    private String name;
    private String address;
    private String registerNumber;
    private String taxNumber;
}
