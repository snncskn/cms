package com.minetec.backend.dto.form;

import com.minetec.backend.entity.OrderItem;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderApproveForm {
    @NotNull
    private UUID orderItemUuid;
    @NotNull
    private BigDecimal approveQuantity;

    private OrderItem orderItem;
}
