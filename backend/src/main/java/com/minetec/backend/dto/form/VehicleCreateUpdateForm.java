package com.minetec.backend.dto.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Sinan
 */
@Data
public class VehicleCreateUpdateForm {

    private UUID uuid;

    @NotNull
    @Length(min = 2, max = 20)
    private String fleetNo;

    @NotNull
    private UUID vehicleTypeUuid;

    @NotNull
    private UUID siteUuid;

    @NotNull
    @NotEmpty
    private String vinNo;

    @NotNull
    @NotEmpty
    private String serialNo;

    @NotNull
    @NotEmpty
    private String unit;

    @NotNull
    @NotEmpty
    private String registrationNo;

    private boolean isUsable;
    private String currentMachineHours;
    @NotNull
    private String serviceIntervalHours;
    private String lastServiceDate;
    private String lastServiceHours;

}
