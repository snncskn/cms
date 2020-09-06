package com.minetec.backend.repository.projection.warehouse;

import com.minetec.backend.repository.projection.ItemTypeListItemProjection;

import java.math.BigDecimal;
import java.util.UUID;

public interface TransferItemListItemProjection {

    UUID getUuid();

    TransferListItemProjection getTransfer();

    ItemTypeListItemProjection getItemType();

    String getBarcode();

    String getDescription();

    BigDecimal getQuantity();

    BigDecimal getTotalQuantity();

    boolean getApprove();

    BigDecimal getApproveQuantity();
}
