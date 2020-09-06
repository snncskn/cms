package com.minetec.backend.repository.projection;

import java.util.UUID;

public interface ItemListItemProjection {

    String getStorePartNumber();
    UUID getUuid();
    String getItemDescription();
    String getBarcode();
    Integer getMinStockQuantity();
    String getUnit();
}
