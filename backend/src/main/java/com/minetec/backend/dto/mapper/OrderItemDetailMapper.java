package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.OrderItemDetailInfo;
import com.minetec.backend.entity.OrderItemDetail;
import com.minetec.backend.util.Utils;

public class OrderItemDetailMapper {

    public static OrderItemDetailInfo toInfo(final OrderItemDetail orderItemDetail) {
        var info = new OrderItemDetailInfo();
        info.setDescription(orderItemDetail.getDescription());
        info.setQuantity(orderItemDetail.getQuantity());
        info.setOrderItemInfo(OrderItemMapper.toInfo(orderItemDetail.getOrderItem()));
        info.setCreatedDate(Utils.toString(orderItemDetail.getCreatedAt()));
        info.setCreatedBy(orderItemDetail.getCreatedBy());
        return info;
    }
}
