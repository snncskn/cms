package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.VehicleAttributeValueInfo;
import com.minetec.backend.entity.VehicleAttributeValue;

public class VehicleAttributeValueMapper {

    /**
     * @param vehicleAttributeValue
     * @return
     */
    public static VehicleAttributeValueInfo toInfo(final VehicleAttributeValue vehicleAttributeValue) {
        var info = new VehicleAttributeValueInfo();
        info.setUuid(vehicleAttributeValue.getUuid());
        info.setVehicleAttributeValue(vehicleAttributeValue.getValue());
        return info;
    }
}
