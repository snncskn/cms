package com.minetec.backend.dto.mapper.warehouse;

import com.minetec.backend.dto.info.warehouse.TransferInfo;
import com.minetec.backend.dto.mapper.SiteMapper;
import com.minetec.backend.entity.warehouse.Transfer;
import com.minetec.backend.repository.projection.warehouse.TransferListItemProjection;
import com.minetec.backend.util.Utils;

/**
 * @author Sinan
 */

public class TransferMapper {

    /**
     * @param transfer
     * @return
     */
    public static TransferInfo toInfo(final Transfer transfer) {
        var info = new TransferInfo();
        info.setTransferDate(Utils.toString(transfer.getTransferDate()));
        info.setTransferNumber(transfer.getTransferNumber());
        info.setRejectionReason(transfer.getRejectionReason());
        info.setUuid(transfer.getUuid());
        info.setStatus(transfer.getStatus().name());
        info.setSourceSiteInfo(SiteMapper.toInfo(transfer.getSourceSite()));
        info.setTargetSiteInfo(SiteMapper.toInfo(transfer.getTargetSite()));
        return info;
    }

    public static Transfer toMap(final TransferListItemProjection transferListItemProjection) {
        var transfer = new Transfer();
        transfer.setStatus(transferListItemProjection.getStatus());
        transfer.setTransferDate(transferListItemProjection.getTransferDate());
        transfer.setTransferNumber(transferListItemProjection.getTransferNumber());
        transfer.setRejectionReason(transferListItemProjection.getRejectionReason());
        transfer.setDeliverDate(transferListItemProjection.getDeliverDate());
        transfer.setRejectionReason(transferListItemProjection.getRejectionReason());
        transfer.setRequestDate(transferListItemProjection.getRequestDate());
        return transfer;
    }
}
