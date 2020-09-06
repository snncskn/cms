package com.minetec.backend.dto.info;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class PropertyInfo {

    @NotNull
    private UUID uuid;

    @NotNull
    @Length(min = 3, max = 50)
    private String groupName;

    @NotNull
    @Length(min = 2, max = 50)
    private String keyLabel;

    @NotNull
    @Length(min = 2, max = 50)
    private String keyValue;

}
