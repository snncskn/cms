package com.minetec.backend.dto.info.workshop;

import com.minetec.backend.dto.info.BasicUserInfo;
import com.minetec.backend.dto.info.ItemInfo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobCardItemDeliveredInfo {

    private ItemInfo itemInfo;
    private String jobCardItemStatus;
    private BigDecimal quantity;
    private BigDecimal approveQuantity;
    private BasicUserInfo receivedUserInfo;
    private BasicUserInfo requestUserInfo;
    private BasicUserInfo deliveredUserInfo;
    private String requestDate;
    private String deliveredDate;
    private String description;
}
