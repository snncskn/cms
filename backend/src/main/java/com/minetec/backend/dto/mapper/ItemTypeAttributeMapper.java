package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ItemAttributeValueInfo;
import com.minetec.backend.dto.info.ItemTypeAttributeInfo;
import com.minetec.backend.entity.ItemTypeAttribute;

import java.util.ArrayList;
import java.util.List;

public class ItemTypeAttributeMapper {

    /**
     * @param itemTypeAttributes
     * @return
     */
    public static List<ItemTypeAttributeInfo> toInfos(final List<ItemTypeAttribute> itemTypeAttributes) {
        var infos = new ArrayList<ItemTypeAttributeInfo>();
        itemTypeAttributes.forEach(itemTypeAttribute -> {
            infos.add(toInfo(itemTypeAttribute));
        });
        return infos;
    }

    /**
     * toInfo
     *
     * @param itemTypeAttribute
     * @return
     */

    public static ItemTypeAttributeInfo toInfo(final ItemTypeAttribute itemTypeAttribute) {
        var itemTypeAttributeInfo = new ItemTypeAttributeInfo();
        itemTypeAttributeInfo.setUuid(itemTypeAttribute.getItemAttribute().getUuid());
        itemTypeAttributeInfo.setVehicleAttributeName(itemTypeAttribute.getItemAttribute().getName());
        var itemAttributeValueInfos = new ArrayList<ItemAttributeValueInfo>();
        itemTypeAttribute.getItemAttribute().getItemAttributeValues().forEach(itemAttributeValue -> {
            itemAttributeValueInfos.add(ItemAttributeValueMapper.toInfo(itemAttributeValue));
        });
        itemTypeAttributeInfo.setVehicleAttributeValues(itemAttributeValueInfos);
        return itemTypeAttributeInfo;
    }

}
