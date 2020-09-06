package com.minetec.backend.dto.form.warehouse;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferApproveForm {
    private UUID transferItemUuid;
    private BigDecimal approveQuantity;
}
