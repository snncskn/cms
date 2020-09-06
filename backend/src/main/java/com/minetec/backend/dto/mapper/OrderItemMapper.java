package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.OrderItemInfo;
import com.minetec.backend.entity.OrderItem;

/**
 * @author Sinan
 */

public class OrderItemMapper {

    public static OrderItemInfo toInfo(final OrderItem entity) {
        var info = new OrderItemInfo();
        info.setId(entity.getId());
        info.setUuid(entity.getUuid());
        info.setItemInfo(ItemMapper.toInfo(entity.getItem()));
        info.setOrderInfo(OrderMapper.toInfo(entity.getOrder()));
        info.setBarcode(entity.getBarcode());
        info.setDescription(entity.getDescription());
        info.setDiscountPercent(entity.getDiscountPercent());
        info.setQuantity(entity.getQuantity());
        info.setTaxPercent(entity.getTaxPercent());
        info.setTaxTotal(entity.getTaxTotal());
        info.setTotal(entity.getTotal());
        info.setTotalQuantity(entity.getTotalQuantity());
        info.setUnitPrice(entity.getUnitPrice());
        info.setUuid(entity.getUuid());
        info.setUnit(entity.getUnit());
        info.setApproveQuantity(entity.getApproveQuantity());
        info.setApprove(entity.isApprove());
        info.setMessageInfos(MessageMapper.toInfos(entity.getMessages()));
        return info;
    }

}
