package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Sinan
 */
@Data
public class OrderItemCreateUpdateForm {

    private UUID uuid;
    private UUID itemUuid;
    private UUID orderUuid;
    private String description;
    private String barcode;
    private String totalQuantity;
    private String totalAmount;
    private String discount;

    private @NotNull String quantity;
    private @NotNull String unitPrice;
    private @NotNull String discountPercent;
    private @NotNull String taxPercent;
    private @NotNull String taxTotal;
    private @NotNull String total;
    private @NotNull String unit;
    private @NotNull String approveQuantity;
    private boolean approve;
}
