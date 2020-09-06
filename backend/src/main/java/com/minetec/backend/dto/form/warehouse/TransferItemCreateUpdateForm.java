package com.minetec.backend.dto.form.warehouse;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Sinan
 */
@Data
public class TransferItemCreateUpdateForm {

    private UUID uuid;
    private UUID itemUuid;
    private UUID transferUuid;
    private String description;
    private String totalQuantity;
    private @NotNull String quantity;
    private @NotNull String approveQuantity;
    private boolean approve;
}
