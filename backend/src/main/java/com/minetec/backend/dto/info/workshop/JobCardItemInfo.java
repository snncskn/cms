package com.minetec.backend.dto.info.workshop;

import com.minetec.backend.dto.info.BasicUserInfo;
import com.minetec.backend.dto.info.ItemInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class JobCardItemInfo {

    private UUID uuid;
    private JobCardInfo jobCardInfo;
    private ItemInfo itemInfo;
    private String jobCardItemStatus;
    private BasicUserInfo receivedUserInfo;
    private BasicUserInfo requestUserInfo;
    private BasicUserInfo deliveredUserInfo;
    private String requestDate;
    private String deliveredDate;
    private String description;

    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal approveQuantity = BigDecimal.ZERO;
    private BigDecimal deliveredQuantity = BigDecimal.ZERO;
    private BigDecimal remainingQuantity = BigDecimal.ZERO;
    private BigDecimal availableQuantity = BigDecimal.ZERO;

    private Long sourceSiteId;
    private Long jobCardItemId;
}
