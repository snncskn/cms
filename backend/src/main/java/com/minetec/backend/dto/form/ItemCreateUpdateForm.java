package com.minetec.backend.dto.form;

import com.minetec.backend.dto.info.ImageInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @author Brandon Tabe
 */

@Data
public class ItemCreateUpdateForm {
    private UUID uuid;
    private UUID partTypeUuid;
    private String storePartNumber;
    private String itemDescription;
    private String barcode;
    private BigDecimal minStockQuantity;
    private String unit;
    private List<ImageInfo> imageInfos;
}
