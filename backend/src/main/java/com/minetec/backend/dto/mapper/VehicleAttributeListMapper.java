package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.VehicleAttributeListDetailInfo;
import com.minetec.backend.dto.info.VehicleAttributeListInfo;
import com.minetec.backend.entity.VehicleAttributeList;

import java.util.ArrayList;

public class VehicleAttributeListMapper {

    /**
     * @param vehicleAttributeLists
     * @return
     */
    public static VehicleAttributeListInfo toInfo(final ArrayList<VehicleAttributeList> vehicleAttributeLists) {
        var vehicleAttributeListInfo = new VehicleAttributeListInfo();
        var vehicleAttributeListDetailInfos = new ArrayList<VehicleAttributeListDetailInfo>();
        vehicleAttributeLists.forEach(vehicleAttributeList -> {
            var vehicleAttributeListDetailInfo = new VehicleAttributeListDetailInfo();
            vehicleAttributeListDetailInfo.setVehicleAttributeInfo(
                VehicleAttributeMapper.toVehicleDetailInfo(vehicleAttributeList.getVehicleAttribute()));
            vehicleAttributeListDetailInfo.setVehicleAttributeValueInfo(
                VehicleAttributeValueMapper.toInfo(vehicleAttributeList.getVehicleAttributeValue()));
            vehicleAttributeListDetailInfos.add(vehicleAttributeListDetailInfo);
        });
        vehicleAttributeListInfo.setVehicleDetailTypeAttrInfos(vehicleAttributeListDetailInfos);
        return vehicleAttributeListInfo;
    }

}
