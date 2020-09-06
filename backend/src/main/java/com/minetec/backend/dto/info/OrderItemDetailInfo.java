package com.minetec.backend.dto.info;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDetailInfo {

    private OrderItemInfo orderItemInfo;
    private BigDecimal quantity;
    private String description;
    private String createdUser;
    private String createdDate;
    private Long createdBy;
}
