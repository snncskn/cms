package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ItemTypeInfo;
import com.minetec.backend.entity.ItemType;
import org.springframework.data.domain.Page;

import java.util.ArrayList;

public class ItemTypeMapper {

    /**
     * @param itemType
     * @return
     */
    public static ItemTypeInfo toInfo(final ItemType itemType) {
        var typeInfo = new ItemTypeInfo();
        typeInfo.setName(itemType.getName());
        typeInfo.setUuid(itemType.getUuid());
        return typeInfo;
    }

    /**
     * @param items
     * @return
     */
    public static ArrayList<ItemTypeInfo> toInfos(final Page<ItemType> items) {
        var newItems = new ArrayList<ItemTypeInfo>();
        items.forEach(itemType -> newItems.add(toInfo(itemType)));
        return newItems;
    }
}
