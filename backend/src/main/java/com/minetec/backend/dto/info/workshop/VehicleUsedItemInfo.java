package com.minetec.backend.dto.info.workshop;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleUsedItemInfo {

    private String stockCode;
    private String itemDescription;
    private String itemType;
    private String deliverDate;
    private String jobCardNumber;
    private BigDecimal usedQuantity;
}
