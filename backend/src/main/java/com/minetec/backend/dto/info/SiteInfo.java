package com.minetec.backend.dto.info;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class SiteInfo {

    @NotNull
    @Length(min = 2)
    private String description;
    @NotNull
    private UUID uuid;

    private SupplierInfo supplierInfo;

    private Long supplierId;
}
