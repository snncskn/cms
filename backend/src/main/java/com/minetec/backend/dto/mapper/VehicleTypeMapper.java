package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.VehicleTypeAttributeInfo;
import com.minetec.backend.dto.info.VehicleTypeInfo;
import com.minetec.backend.entity.VehicleType;

import java.util.ArrayList;

public class VehicleTypeMapper {

    /**
     * @param vehicleType
     * @return
     */
    public static VehicleTypeInfo toInfo(final VehicleType vehicleType) {
        var vehicleTypeInfo = new VehicleTypeInfo();
        vehicleTypeInfo.setName(vehicleType.getName());
        vehicleTypeInfo.setUuid(vehicleType.getUuid());
        return vehicleTypeInfo;
    }

    public static VehicleTypeInfo toMap(final VehicleType vehicleType) {
        var vehicleTypeInfo = toInfo(vehicleType);
        var attributeInfoList = new ArrayList<VehicleTypeAttributeInfo>();
        vehicleType.getVehicleTypeAttributes().forEach(vehicleTypeAttribute -> {
            attributeInfoList.add(VehicleTypeAttributeMapper.toInfo(vehicleTypeAttribute));
        });
        vehicleTypeInfo.setVehicleTypeAttributes(attributeInfoList);
        return vehicleTypeInfo;
    }
}
