package com.minetec.backend.dto.filter;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ItemOrderFilterForm {
    @NotNull
    private UUID itemUuid;
    private String startDate;
    private String endDate;
    private String invoiceNumber = "";
    private String supplierName  = "";
    private BigDecimal quantity;
    private BigDecimal total;
    private int page;
    private int size;
}
