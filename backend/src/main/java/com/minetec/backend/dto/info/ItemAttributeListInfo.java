package com.minetec.backend.dto.info;

import lombok.Data;

import java.util.List;

/**
 * @author Sinan
 */

@Data
public class ItemAttributeListInfo {
    private ItemInfo partInfo;
    private List<ItemAttributeListDetailInfo> partDetailTypeAttrInfos;
}
