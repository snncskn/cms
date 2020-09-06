package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.VehicleAttributeValueInfo;
import com.minetec.backend.dto.info.VehicleTypeAttributeInfo;
import com.minetec.backend.entity.VehicleTypeAttribute;

import java.util.ArrayList;
import java.util.List;

public class VehicleTypeAttributeMapper {

    /**
     * @param vehicleTypeAttributes
     * @return
     */
    public static List<VehicleTypeAttributeInfo> toInfos(final List<VehicleTypeAttribute> vehicleTypeAttributes) {
        var infos = new ArrayList<VehicleTypeAttributeInfo>();
        vehicleTypeAttributes.forEach(vehicleTypeAttribute -> infos.add(toInfo(vehicleTypeAttribute)));
        return infos;
    }

    /**
     * toInfo
     *
     * @param vehicleTypeAttribute
     * @return
     */

    public static VehicleTypeAttributeInfo toInfo(final VehicleTypeAttribute vehicleTypeAttribute) {
        var vehicleTypeAttributeInfo = new VehicleTypeAttributeInfo();
        vehicleTypeAttributeInfo.setUuid(vehicleTypeAttribute.getVehicleAttribute().getUuid());
        vehicleTypeAttributeInfo.setVehicleAttributeName(vehicleTypeAttribute.getVehicleAttribute().getName());
        var vehicleAttributeValueInfos = new ArrayList<VehicleAttributeValueInfo>();
        vehicleTypeAttribute.getVehicleAttribute().getVehicleAttributeValues().forEach(vehicleAttributeValue ->
            vehicleAttributeValueInfos.add(VehicleAttributeValueMapper.toInfo(vehicleAttributeValue))
        );
        vehicleTypeAttributeInfo.setVehicleAttributeValues(vehicleAttributeValueInfos);
        return vehicleTypeAttributeInfo;
    }
}
