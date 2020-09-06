package com.minetec.backend.dto.info.warehouse;

import com.minetec.backend.dto.info.ItemInfo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransferItemInfo {

    @NotNull
    private UUID uuid;
    private TransferInfo transferInfo;
    private ItemInfo itemInfo;
    private String description;
    private BigDecimal quantity;
    private BigDecimal totalQuantity;
    private BigDecimal approveQuantity;
    private boolean approve;
}
