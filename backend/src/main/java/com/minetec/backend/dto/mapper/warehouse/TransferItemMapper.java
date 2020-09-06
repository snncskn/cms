package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.warehouse.TransferItemInfo;
import com.minetec.backend.dto.mapper.warehouse.TransferMapper;
import com.minetec.backend.entity.warehouse.TransferItem;

/**
 * @author Sinan
 */

public class TransferItemMapper {

    public static TransferItemInfo toInfo(final TransferItem entity) {
        var info = new TransferItemInfo();
        info.setUuid(entity.getUuid());
        info.setItemInfo(ItemMapper.toInfo(entity.getItem()));
        info.setTransferInfo(TransferMapper.toInfo(entity.getTransfer()));
        info.setDescription(entity.getDescription());
        info.setQuantity(entity.getQuantity());
        //info.setTotalQuantity(entity.getTotalQuantity());
        info.setUuid(entity.getUuid());
        info.setApproveQuantity(entity.getApproveQuantity());
        info.setApprove(entity.isApprove());
        return info;
    }

}
