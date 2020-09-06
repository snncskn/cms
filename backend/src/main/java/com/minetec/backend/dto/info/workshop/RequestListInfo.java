package com.minetec.backend.dto.info.workshop;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 */
@Data
public class RequestListInfo {
    private UUID uuid;
    private String reportNumber = "";
    private String fleetNumber = "";
    private String barcode = "";
    private String itemDescription = "";
    private String requestUser = "";
    private String requestDate = "";
    private String deliveredDate = "";
    private String deliveryTime = "";
    private String stockCode = "";
    private String description = "";
    private String jobCardItemStatus = "";
    private BigDecimal approveQuantity = BigDecimal.ZERO;
    private BigDecimal requestedQuantity = BigDecimal.ZERO;
    private BigDecimal deliveredQuantity = BigDecimal.ZERO;
    private BigDecimal remainingQuantity = BigDecimal.ZERO;
    private BigDecimal availableQuantity = BigDecimal.ZERO;
    private UUID receivedUserUuid;
    private Long siteId = 0L;
    private Long itemId = 0L;
    private UUID jobCardUuid;
}
