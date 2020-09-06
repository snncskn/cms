package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.VehicleInfo;
import com.minetec.backend.dto.info.workshop.VehicleUsedItemInfo;
import com.minetec.backend.entity.StockHistory;
import com.minetec.backend.entity.Vehicle;
import com.minetec.backend.util.Utils;

/**
 * @author Sinan
 */

public class VehicleMapper {

    public static VehicleInfo toInfo(final Vehicle vehicle) {
        var info = new VehicleInfo();
        info.setUuid(vehicle.getUuid());
        info.setFleetNo(vehicle.getFleetNo());
        info.setRegistrationNo(vehicle.getRegistrationNo());
        info.setUnit(vehicle.getUnit());
        info.setSerialNo(vehicle.getSerialNo());
        info.setVinNo(vehicle.getVinNo());
        info.setCurrentMachineHours(vehicle.getCurrentMachineHours());
        info.setLastServiceDate(Utils.toString(vehicle.getLastServiceDate()));
        info.setLastServiceHours(vehicle.getLastServiceHours());
        info.setServiceIntervalHours(vehicle.getServiceIntervalHours());
        info.setUnit(vehicle.getUnit());
        info.setUsable(vehicle.isUsable());
        info.setRemainingHours(Utils.toInteger(vehicle.getRemainingHours()));
        info.setVehicleType(VehicleTypeMapper.toInfo(vehicle.getVehicleType()));

        return info;
    }

    public static VehicleInfo toMap(final Vehicle vehicle) {
        var info = toInfo(vehicle);
        info.setVehicleType(VehicleTypeMapper.toInfo(vehicle.getVehicleType()));
        info.setSiteInfo(SiteMapper.toInfo(vehicle.getSite()));
        return info;
    }

    /**
     * @param stockHistory
     * @return
     */
    public static VehicleUsedItemInfo toMapUsedItem(final StockHistory stockHistory) {
        final var items = new VehicleUsedItemInfo();
        items.setDeliverDate(Utils.toString(stockHistory.getJobCardItem().getDeliveredDate()));
        items.setItemDescription(stockHistory.getJobCardItem().getItem().getItemDescription());
        items.setItemType(stockHistory.getJobCardItem().getItem().getItemType().getName());
        items.setJobCardNumber(stockHistory.getJobCardItem().getJobCard().getReportNumber());
        items.setStockCode(stockHistory.getJobCardItem().getItem().getStorePartNumber());
        items.setUsedQuantity(stockHistory.getQuantity());
        return items;
    }
}
