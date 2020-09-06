package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.VehicleAttributeInfo;
import com.minetec.backend.dto.info.VehicleAttributeValueInfo;
import com.minetec.backend.entity.VehicleAttribute;

import java.util.ArrayList;

public class VehicleAttributeMapper {

    /**
     * @param vehicleAttribute
     * @return
     */
    public static VehicleAttributeInfo toInfo(final VehicleAttribute vehicleAttribute) {
        var info = new VehicleAttributeInfo();
        info.setUuid(vehicleAttribute.getUuid());
        info.setName(vehicleAttribute.getName());
        var vehicleAttributeValueInfos = new ArrayList<VehicleAttributeValueInfo>();
        vehicleAttribute.getVehicleAttributeValues().forEach(vehicleAttributeValue -> {
            var vehicleAttributeValueInfo = new VehicleAttributeValueInfo();
            vehicleAttributeValueInfo.setUuid(vehicleAttributeValue.getUuid());
            vehicleAttributeValueInfo.setVehicleAttributeValue(vehicleAttributeValue.getValue());
            vehicleAttributeValueInfos.add(vehicleAttributeValueInfo);
        });

        info.setVehicleAttributeValues(vehicleAttributeValueInfos);
        return info;
    }

    /**
     * toVehicleDetailInfo
     *
     * @param vehicleAttribute
     * @return
     */
    public static VehicleAttributeInfo toVehicleDetailInfo(final VehicleAttribute vehicleAttribute) {
        var vehicleAttributeInfo = new VehicleAttributeInfo();
        vehicleAttributeInfo.setUuid(vehicleAttribute.getUuid());
        vehicleAttributeInfo.setName(vehicleAttribute.getName());
        return vehicleAttributeInfo;
    }
}
