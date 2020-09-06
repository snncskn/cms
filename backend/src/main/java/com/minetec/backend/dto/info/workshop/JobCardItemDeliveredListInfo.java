package com.minetec.backend.dto.info.workshop;

import com.minetec.backend.dto.info.BasicUserInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class JobCardItemDeliveredListInfo {

    private UUID jobCardItemUuid;
    //private BigDecimal remainingQuantity;
    private String deliveredDate;
    private BasicUserInfo deliveredUserInfo;
    private BigDecimal deliveredQuantity;
    private BasicUserInfo receivedUserInfo;

    private Long sourceSiteId;
    private Long jobCardItemId;
}
