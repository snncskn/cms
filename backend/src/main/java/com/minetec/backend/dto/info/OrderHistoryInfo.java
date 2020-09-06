package com.minetec.backend.dto.info;

import com.minetec.backend.dto.enums.OrderStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderHistoryInfo {

    private UUID uuid;
    private OrderInfo orderInfo;
    private BasicUserInfo userInfo;
    private String description;
    private OrderStatus status;
}

