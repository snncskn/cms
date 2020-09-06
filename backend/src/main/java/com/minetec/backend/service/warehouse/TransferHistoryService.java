package com.minetec.backend.service.warehouse;

import com.minetec.backend.dto.info.warehouse.TransferHistoryInfo;
import com.minetec.backend.dto.mapper.warehouse.TransferHistoryMapper;
import com.minetec.backend.entity.warehouse.Transfer;
import com.minetec.backend.entity.warehouse.TransferHistory;
import com.minetec.backend.repository.projection.warehouse.TransferHistoryListItemProjection;
import com.minetec.backend.repository.warehouse.TransferHistoryRepository;
import com.minetec.backend.service.EntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransferHistoryService extends EntityService<TransferHistory, TransferHistoryRepository> {

    public Page<TransferHistoryListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    public TransferHistoryInfo create(final Transfer transfer) {
        var entity = new TransferHistory();
        entity.setTransfer(transfer);
        entity.setDescription(transfer.getRejectionReason());
        entity.setStatus(transfer.getStatus());
        entity.setUser(this.getCurrentUser());
        return TransferHistoryMapper.toInfo(this.persist(entity));
    }

}
