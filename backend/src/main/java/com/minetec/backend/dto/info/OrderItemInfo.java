package com.minetec.backend.dto.info;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderItemInfo {

    private Long id;
    private UUID uuid;
    private OrderInfo orderInfo;
    private ItemInfo itemInfo;
    private String barcode;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal discountPercent;
    private BigDecimal taxPercent;
    private BigDecimal totalQuantity;
    private BigDecimal total;
    private BigDecimal taxTotal;
    private String unit;
    private BigDecimal approveQuantity;
    private boolean approve;
    private List<MessageInfo> messageInfos;
}
