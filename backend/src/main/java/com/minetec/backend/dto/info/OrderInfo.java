package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderInfo {

    @NotNull
    private UUID uuid;
    private SupplierInfo supplierInfo;
    private SiteInfo siteInfo;
    private List<OrderItemInfo> orderItemInfos;
    private String status;
    private String requestNumber;
    private String orderNumber;
    private String invoiceNumber;
    private String referenceNumber;
    private String addressDetail;
    private String rejectionReason;
    private String requestDate;
    private String orderCreationDate;
    private String invoiceDate;
    private BigDecimal grandTotal;
    private BigDecimal taxTotal;
    private BigDecimal discountTotal;
    private BigDecimal totalQuantity;
    private BasicUserInfo basicUserInfo;
}
