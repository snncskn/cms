package com.minetec.backend.dto.form;

import lombok.Data;

@Data
public class PropertyCreateUpdateForm {
    private String groupName;
    private String keyLabel;
    private String keyValue;
}
