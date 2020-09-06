package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ItemOrderInfo;
import com.minetec.backend.entity.OrderItem;
import com.minetec.backend.util.Utils;

public class ItemOrderMapper {

    public static ItemOrderInfo toMap(final OrderItem orderItem) {
        var itemOrderInfo = new ItemOrderInfo();
        itemOrderInfo.setQuantity(orderItem.getQuantity());
        itemOrderInfo.setRequestDate(Utils.toString(orderItem.getOrder().getRequestDate()));
        itemOrderInfo.setInvoiceNumber(orderItem.getOrder().getInvoiceNumber());
        itemOrderInfo.setSupplierName(orderItem.getOrder().getSupplier().getName());
        itemOrderInfo.setTotal(orderItem.getTotal());
        itemOrderInfo.setOrderItemUuid(orderItem.getUuid());
        return itemOrderInfo;
    }
}
