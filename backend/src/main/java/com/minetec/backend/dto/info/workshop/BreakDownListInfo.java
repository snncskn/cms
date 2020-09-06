package com.minetec.backend.dto.info.workshop;

import com.minetec.backend.dto.info.BasicUserInfo;
import com.minetec.backend.dto.info.ImageInfo;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BreakDownListInfo {

    private UUID uuid;
    private String breakDownStartDate;
    private String breakDownEndDate;
    private String breakDiff;
    private String jobCardStartDate;
    private String jobCardEndDate;
    private String jobDiff;
    private String lastUpdateDate;
    private String fleetNumber;
    private String reportNumber;
    private String jobCardStatusText;
    private String jobCardStatus;
    private String siteName;
    private String currentMachineHours;
    private boolean hasTask;
    private String description;
    private BasicUserInfo operatorUser;
    private BasicUserInfo supervisorUser;
    private List<ImageInfo> imageInfos;

}
