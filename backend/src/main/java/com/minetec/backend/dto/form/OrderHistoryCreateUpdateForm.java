package com.minetec.backend.dto.form;

import com.minetec.backend.dto.enums.OrderStatus;
import lombok.Data;

import java.util.UUID;

/**
 * @author Brandon Tabe
 */

@Data
public class OrderHistoryCreateUpdateForm {
    private UUID uuid;
    private UUID orderUuid;
    private String description;
    private OrderStatus status;
}
