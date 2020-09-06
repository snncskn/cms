package com.minetec.backend.repository.projection;

import com.minetec.backend.dto.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Sinan
 */
public interface OrderListItemProjection {

    UUID getUuid();

    List<SupplierListItemProjection> getSupplier();

    List<SiteListItemProjection> getSite();

    OrderStatus getStatus();

    String getRequestNumber();

    String getOrderNumber();

    String getInvoiceNumber();

    String getReferenceNumber();

    String getAddressDetail();

    String getRejectionReason();

    LocalDateTime getRequestDate();

    LocalDateTime getOrderCreationDate();

    LocalDateTime getInvoiceDate();

    BigDecimal getGrandTotal();

    BigDecimal getTaxTotal();

    BigDecimal getDiscountTotal();

    BigDecimal getTotalQuantity();

}
