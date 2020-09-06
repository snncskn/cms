package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.OrderInfo;
import com.minetec.backend.entity.Order;
import com.minetec.backend.util.Utils;

/**
 * @author Sinan
 */

public class OrderMapper {

    /**
     * @param order
     * @return
     */
    public static OrderInfo toInfo(final Order order) {
        var info = new OrderInfo();
        info.setAddressDetail(order.getAddressDetail());
        info.setDiscountTotal(order.getDiscountTotal());
        info.setGrandTotal(order.getGrandTotal());
        info.setInvoiceDate(Utils.toString(order.getInvoiceDate()));
        info.setInvoiceNumber(order.getInvoiceNumber());
        info.setOrderCreationDate(Utils.toString(order.getOrderCreationDate()));
        info.setOrderNumber(order.getOrderNumber());
        info.setReferenceNumber(order.getReferenceNumber());
        info.setRejectionReason(order.getRejectionReason());
        info.setRequestDate(Utils.toString(order.getRequestDate()));
        info.setRequestNumber(order.getRequestNumber());
        info.setTaxTotal(order.getTaxTotal());
        info.setTotalQuantity(order.getTotalQuantity());
        info.setUuid(order.getUuid());
        info.setStatus(order.getStatus().name());
        info.setSiteInfo(SiteMapper.toInfo(order.getSite()));
        info.setSupplierInfo(SupplierMapper.toInfo(order.getSupplier()));
        return info;
    }

}
