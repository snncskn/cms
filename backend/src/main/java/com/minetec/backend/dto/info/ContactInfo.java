package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class ContactInfo {

    @NotNull
    private UUID uuid;

    private String name;
    private String role;
    private String email;
    private String phoneNo;
    private String landLine;
}
