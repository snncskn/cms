package com.minetec.backend.dto.info;

import com.minetec.backend.repository.projection.OrderListItemProjection;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class OrderListResponseInfo {
    private Page<OrderListItemProjection> orderListItemProjections;
    private Long requestCounts = 0L;
    private Long orderCounts = 0L;
    private Long invoiceCounts = 0L;
    private Long rejectCounts = 0L;
}
