package com.minetec.backend.dto.form.workshop;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class JobCardItemCreateForm {
    private UUID jobCardUuid;
    private UUID itemUuid;
    private BigDecimal quantity;
    private BigDecimal approveQuantity;
    private String description;
}
