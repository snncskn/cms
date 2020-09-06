package com.minetec.backend.dto.info;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ItemInfo {

    private UUID uuid;
    private String storePartNumber;
    private ItemTypeInfo itemTypeInfo;
    private String itemDescription;
    private String barcode;
    private BigDecimal minStockQuantity;
    private List<ImageInfo> imageInfos;
    private String unit;
    private BigDecimal currentQuantity;
    private BigDecimal price;
    private Long itemId;
    private boolean stockLevelWarning;
}
