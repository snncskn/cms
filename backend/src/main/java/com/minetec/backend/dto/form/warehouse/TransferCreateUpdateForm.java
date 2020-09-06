package com.minetec.backend.dto.form.warehouse;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Sinan
 */
@Data
public class TransferCreateUpdateForm {

    @NotNull
    private UUID sourceSiteUuid;
    @NotNull
    private UUID targetSiteUuid;
    @NotNull
    private String status;

    private String transferNumber;
    private String quantity;
    private String approveQuantity;
    private String transferDate;
}
