package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ItemAttributeInfo;
import com.minetec.backend.dto.info.ItemAttributeValueInfo;
import com.minetec.backend.entity.ItemAttribute;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;


public class ItemAttributeMapper {

    /**
     * @param itemAttribute
     * @return
     */
    public static ItemAttributeInfo toInfo(final ItemAttribute itemAttribute) {
        var info = new ItemAttributeInfo();
        info.setUuid(itemAttribute.getUuid());
        info.setName(itemAttribute.getName());
        var itemAttributeValueInfos = new ArrayList<ItemAttributeValueInfo>();
        itemAttribute.getItemAttributeValues().forEach(itemAttributeValue -> {
            var itemAttributeValueInfo = new ItemAttributeValueInfo();
            itemAttributeValueInfo.setUuid(itemAttributeValue.getUuid());
            itemAttributeValueInfo.setVehicleAttributeValue(itemAttributeValue.getValue());
            itemAttributeValueInfos.add(itemAttributeValueInfo);
        });
        info.setVehicleAttributeValues(itemAttributeValueInfos);
        return info;
    }

    /**
     * toItemDetailInfo
     *
     * @param itemAttribute
     * @return
     */
    public static ItemAttributeInfo toItemDetailInfo(final ItemAttribute itemAttribute) {
        var info = new ItemAttributeInfo();
        info.setUuid(itemAttribute.getUuid());
        info.setName(itemAttribute.getName());
        return info;
    }

    /**
     * @param items
     * @return
     */
    public static List<ItemAttributeInfo> toInfos(final Page<ItemAttribute> items) {
        var newItems = new ArrayList<ItemAttributeInfo>();
        items.forEach(itemAttribute -> {
            newItems.add(toInfo(itemAttribute));
        });
        return newItems;
    }
}
