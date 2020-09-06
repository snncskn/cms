package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class RoleCreateUpdateForm {

    private UUID uuid;

    @NotNull
    @NotEmpty
    private String name;
}
