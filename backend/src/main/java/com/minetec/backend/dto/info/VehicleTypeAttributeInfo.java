package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class VehicleTypeAttributeInfo {

    @NotNull
    private UUID uuid;
    private String vehicleAttributeName;
    private List<VehicleAttributeValueInfo> vehicleAttributeValues;
    private UUID selectedVehicleAttrUuid;


    /**
     * @author Sinan
     */
    public static class VehicleInfo {

        private UUID uuid;
        private String fleetNo;
        private SiteInfo site;
        private VehicleTypeInfo vehicleType;
        private String vinNo;
        private String unit;
        private String serialNo;
        private String registrationNo;
        private String currentMachineHours;
        private String serviceIntervalHours;
        private String lastServiceDay;
        private String lastServiceHours;

    }
}
