package com.minetec.backend.repository.projection;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface OrderItemListItemProjection {

    UUID getUuid();

    OrderListItemProjection getOrder();

    ItemTypeListItemProjection getItemType();

    String getBarcode();

    String getDescription();

    BigDecimal getQuantity();

    BigDecimal getUnitPrice();

    BigDecimal getDiscountPercent();

    BigDecimal getTaxPercent();

    BigDecimal getTotalQuantity();

    BigDecimal getTotal();

    BigDecimal getTaxTotal();

    String getUnit();

    boolean getApprove();

    BigDecimal getApproveQuantity();

    List<MessageListItemProjection> getMessages();
}
