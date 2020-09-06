package com.minetec.backend.dto.info;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ItemOrderInfo {
    private UUID orderItemUuid;
    private String requestDate;
    private String invoiceNumber;
    private String supplierName;
    private BigDecimal quantity;
    private BigDecimal total;
}
