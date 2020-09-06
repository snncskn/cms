package com.minetec.backend.dto.info.warehouse;

import com.minetec.backend.repository.projection.warehouse.TransferListItemProjection;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class TransferListResponseInfo {
    private Page<TransferListItemProjection> transferListItemProjections;
    private Long requestCounts;
    private Long transferCounts;
    private Long deliverCounts;
    private Long rejectCounts;
}
