package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.ItemAttributeListDetailInfo;
import com.minetec.backend.dto.info.ItemAttributeListInfo;
import com.minetec.backend.entity.ItemAttributeList;

import java.util.ArrayList;

public class ItemAttributeListMapper {

    /**
     * @param itemAttributeLists
     * @return
     */
    public static ItemAttributeListInfo toInfo(final ArrayList<ItemAttributeList> itemAttributeLists) {
        var itemAttributeListInfo = new ItemAttributeListInfo();
        var itemAttributeListDetailInfos = new ArrayList<ItemAttributeListDetailInfo>();

        itemAttributeLists.forEach(itemAttributeList -> {
            var itemAttributeListDetailInfo = new ItemAttributeListDetailInfo();
            itemAttributeListDetailInfo.setVehicleAttributeInfo(
                ItemAttributeMapper.toItemDetailInfo(itemAttributeList.getItemAttribute()));
            itemAttributeListDetailInfo.setVehicleAttributeValueInfo(
                ItemAttributeValueMapper.toInfo(itemAttributeList.getItemAttributeValue()));
            itemAttributeListDetailInfos.add(itemAttributeListDetailInfo);
        });

        itemAttributeListInfo.setPartDetailTypeAttrInfos(itemAttributeListDetailInfos);
        return itemAttributeListInfo;
    }
}
