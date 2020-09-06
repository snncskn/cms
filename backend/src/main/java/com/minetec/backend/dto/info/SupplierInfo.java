package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class SupplierInfo {

    @NotNull
    private UUID uuid;
    private String name;
    private String address;
    private String registerNumber;
    private String taxNumber;
    private List<ContactInfo> contacts;
    private List<ImageInfo> imageInfos;
}

