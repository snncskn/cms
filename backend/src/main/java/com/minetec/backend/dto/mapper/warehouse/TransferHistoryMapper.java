package com.minetec.backend.dto.mapper.warehouse;

import com.minetec.backend.dto.info.warehouse.TransferHistoryInfo;
import com.minetec.backend.dto.mapper.UserMapper;
import com.minetec.backend.entity.warehouse.TransferHistory;

public class TransferHistoryMapper {

    public static TransferHistoryInfo toInfo(final TransferHistory entity) {
        var info = new TransferHistoryInfo();
        info.setUuid(entity.getUuid());
        info.setTransferInfo(TransferMapper.toInfo(entity.getTransfer()));
        info.setDescription(entity.getDescription());
        info.setStatus(entity.getStatus());
        info.setUserInfo(UserMapper.toBasicInfo(entity.getUser()));
        return info;
    }

}
