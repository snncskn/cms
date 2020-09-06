package com.minetec.backend.dto.mapper.workshop;

import com.minetec.backend.dto.info.workshop.BreakDownListInfo;
import com.minetec.backend.dto.mapper.ImageMapper;
import com.minetec.backend.dto.mapper.UserMapper;
import com.minetec.backend.entity.workshop.JobCard;
import com.minetec.backend.util.Utils;

import java.time.LocalDateTime;

public class BreakDownListMapper {

    public static BreakDownListInfo toMap(final JobCard jobCard) {

        var breakDown = new BreakDownListInfo();
        breakDown.setUuid(jobCard.getUuid());
        breakDown.setBreakDownStartDate(Utils.toString(jobCard.getBreakDownStartDate()));
        breakDown.setBreakDownEndDate(Utils.toString(jobCard.getBreakDownEndDate()));
        breakDown.setBreakDiff(Utils.formatterDaysAndHour(jobCard.getBreakDownStartDate(), LocalDateTime.now()));
        breakDown.setJobCardStartDate(Utils.toString(jobCard.getStartDate()));
        breakDown.setJobCardEndDate(Utils.toString(jobCard.getEndDate()));
        breakDown.setJobDiff(Utils.formatterDaysAndHour(jobCard.getStartDate(), LocalDateTime.now()));
        breakDown.setReportNumber(jobCard.getReportNumber());
        breakDown.setJobCardStatus(jobCard.getJobCardStatus().name());
        breakDown.setJobCardStatusText(jobCard.getJobCardStatus().getStatus());
        breakDown.setSiteName(jobCard.getVehicle().getSite().getDescription());
        breakDown.setFleetNumber(jobCard.getVehicle().getFleetNo());
        breakDown.setLastUpdateDate(Utils.toString(jobCard.getModifiedAt()));
        breakDown.setCurrentMachineHours(jobCard.getVehicle().getCurrentMachineHours());
        breakDown.setDescription(jobCard.getDescription());
        breakDown.setImageInfos(ImageMapper.toInfos(jobCard.getImages()));

        if (breakDown.getJobCardStatus().startsWith("ACTIVE")) {
            breakDown.setHasTask(true);
        }

        if (!Utils.isEmpty(jobCard.getOperatorUser())) {
            breakDown.setOperatorUser(UserMapper.toBasicInfo(jobCard.getOperatorUser()));
        }

        if (!Utils.isEmpty(jobCard.getSupervisorUser())) {
            breakDown.setSupervisorUser(UserMapper.toBasicInfo(jobCard.getSupervisorUser()));
        }

        return breakDown;
    }
}
