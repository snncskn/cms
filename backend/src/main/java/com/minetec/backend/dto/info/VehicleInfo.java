package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class VehicleInfo implements Comparable<VehicleInfo> {

    @NotNull
    private UUID uuid;
    private SiteInfo siteInfo;
    private VehicleTypeInfo vehicleType;
    private String fleetNo;
    private String vinNo;
    private String serialNo;
    private String unit;
    private boolean isUsable;
    private String registrationNo;
    private String currentMachineHours;
    private String serviceIntervalHours;
    private String lastServiceDate;
    private String lastServiceHours;
    private boolean hasTask;
    private List<ImageInfo> imageInfos;
    private Integer remainingHours;
    private String serviceWarning;

    @Override
    public int compareTo(final VehicleInfo o) {
        return this.getRemainingHours().compareTo(o.getRemainingHours());
    }
}
