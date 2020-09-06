package com.minetec.backend.dto.info.workshop;

import com.minetec.backend.dto.info.BasicUserInfo;
import lombok.Data;

import java.util.UUID;

@Data
public class JobCardInfo {

    private UUID uuid;
    private String fleetNumber;
    private String kmHour;
    private String currentKmHour;
    private String requestNumber;
    private String description;
    private String vehicleTypeName;
    private String jobCardStartDate;
    private String jobCardEndDate;
    private String jobCardStatus;
    private BasicUserInfo supervisorUser;
    private BasicUserInfo operatorUser;
    private BasicUserInfo mechanicUser;
    private BasicUserInfo foremanUser;
    private boolean riskAssessment;
    private boolean lockOutProcedure;
    private boolean wheelNuts;
    private boolean oilLevel;
    private boolean machineGrease;

}
