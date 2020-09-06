package com.minetec.backend.dto.form.workshop;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class JobCardItemCreateRequestForm {
    private UUID jobCardItemUuid;
    private BigDecimal deliverQuantity;
    private BigDecimal deliveredQuantity;
    private BigDecimal requestedQuantity;
    private UUID receivedUserUuid;
}

