package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.OrderHistoryInfo;
import com.minetec.backend.entity.OrderHistory;

public class OrderHistoryMapper {

    public static OrderHistoryInfo toInfo(final OrderHistory entity) {
        var info = new OrderHistoryInfo();
        info.setUuid(entity.getUuid());
        info.setOrderInfo(OrderMapper.toInfo(entity.getOrder()));
        info.setDescription(entity.getDescription());
        info.setStatus(entity.getStatus());
        info.setUserInfo(UserMapper.toBasicInfo(entity.getUser()));
        return info;
    }

}
