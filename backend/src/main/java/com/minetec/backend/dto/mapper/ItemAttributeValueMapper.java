package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ItemAttributeValueInfo;
import com.minetec.backend.entity.ItemAttributeValue;
import org.springframework.data.domain.Page;

import java.util.ArrayList;

public class ItemAttributeValueMapper {

    /**
     * @param itemAttributeValue
     * @return
     */
    public static ItemAttributeValueInfo toInfo(final ItemAttributeValue itemAttributeValue) {
        var info = new ItemAttributeValueInfo();
        info.setUuid(itemAttributeValue.getUuid());
        info.setVehicleAttributeValue(itemAttributeValue.getValue());
        return info;
    }

    /**
     * @param items
     * @return
     */
    public static ArrayList<ItemAttributeValueInfo> toInfos(final Page<ItemAttributeValue> items) {
        var newItems = new ArrayList<ItemAttributeValueInfo>();
        items.forEach(itemAttributeValue -> {
            newItems.add(toInfo(itemAttributeValue));
        });
        return newItems;
    }
}
