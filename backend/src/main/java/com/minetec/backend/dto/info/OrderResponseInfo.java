package com.minetec.backend.dto.info;

import com.minetec.backend.dto.enums.OrderStatus;
import lombok.Data;

@Data
public class OrderResponseInfo {
    private String responseStatus;
    private OrderStatus orderStatus;
    private String message;
}
