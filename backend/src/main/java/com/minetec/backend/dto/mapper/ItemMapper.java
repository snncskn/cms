package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ItemInfo;
import com.minetec.backend.entity.Item;

/**
 * @author Sinan
 */
public class ItemMapper {

    public static ItemInfo toInfo(final Item entity) {
        var info = new ItemInfo();
        info.setUuid(entity.getUuid());
        info.setStorePartNumber(entity.getStorePartNumber());
        info.setItemDescription(entity.getItemDescription());
        info.setMinStockQuantity(entity.getMinStockQuantity());
        info.setBarcode(entity.getBarcode());
        info.setUnit(entity.getUnit());
        info.setItemId(entity.getId());
        return info;
    }

    public static ItemInfo toMap(final Item entity) {
        var info = toInfo(entity);
        info.setItemTypeInfo(ItemTypeMapper.toInfo(entity.getItemType()));
        return info;
    }

}
