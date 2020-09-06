package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.PropertyInfo;
import com.minetec.backend.entity.Property;

public class PropertyMapper {

    public static PropertyInfo toInfo(final Property property) {
        var info = new PropertyInfo();
        info.setUuid(property.getUuid());
        info.setGroupName(property.getGroupName());
        info.setKeyLabel(property.getKeyLabel());
        info.setKeyValue(property.getKeyValue());
        return info;
    }
}
