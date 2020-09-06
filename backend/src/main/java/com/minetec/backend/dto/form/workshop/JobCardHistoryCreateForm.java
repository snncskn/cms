package com.minetec.backend.dto.form.workshop;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class JobCardHistoryCreateForm {

    @NotNull
    private UUID jobCardUuid;

    @NotNull
    @NotEmpty
    private String description;

}
