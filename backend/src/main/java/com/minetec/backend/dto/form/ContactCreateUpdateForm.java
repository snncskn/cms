package com.minetec.backend.dto.form;

import lombok.Data;

import java.util.UUID;

@Data
public class ContactCreateUpdateForm {

    private UUID uuid;
    private UUID supplierUuid;
    private String name;
    private String role;
    private String email;
    private String phoneNo;
    private String landLine;

}
