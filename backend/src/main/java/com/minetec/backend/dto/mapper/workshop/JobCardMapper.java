package com.minetec.backend.dto.mapper.workshop;

import com.minetec.backend.dto.info.workshop.BreakDownListInfo;
import com.minetec.backend.dto.info.workshop.JobCardInfo;
import com.minetec.backend.dto.mapper.UserMapper;
import com.minetec.backend.entity.workshop.JobCard;
import com.minetec.backend.util.Utils;

import javax.validation.constraints.NotNull;

public class JobCardMapper {

    public static BreakDownListInfo toInfo(@NotNull final JobCard jobCard) {

        var info = new BreakDownListInfo();
        info.setUuid(jobCard.getUuid());
        info.setBreakDownStartDate(Utils.toString(jobCard.getBreakDownStartDate()));
        info.setBreakDownEndDate(Utils.toString(jobCard.getBreakDownEndDate()));
        info.setJobCardStartDate(Utils.toString(jobCard.getStartDate()));
        info.setJobCardEndDate(Utils.toString(jobCard.getEndDate()));
        info.setLastUpdateDate(Utils.toString(jobCard.getModifiedAt()));
        info.setFleetNumber(jobCard.getVehicle().getFleetNo());
        info.setReportNumber(jobCard.getReportNumber());
        info.setJobCardStatus(jobCard.getJobCardStatus().toString());
        info.setSiteName(jobCard.getVehicle().getSite().getDescription());

        return info;
    }

    public static JobCardInfo toJobCardInfo(@NotNull final JobCard jobCard) {
        var info = new JobCardInfo();
        info.setUuid(jobCard.getUuid());
        info.setFleetNumber(jobCard.getVehicle().getFleetNo());
        info.setRequestNumber(jobCard.getReportNumber());
        info.setDescription(jobCard.getDescription());
        info.setKmHour(jobCard.getCurrentKmHour());
        info.setCurrentKmHour(jobCard.getCurrentKmHour());
        info.setVehicleTypeName(jobCard.getVehicle().getVehicleType().getName());
        info.setJobCardStartDate(Utils.toString(jobCard.getStartDate()));
        info.setJobCardEndDate(Utils.toString(jobCard.getStartDate()));
        info.setOperatorUser(UserMapper.toBasicInfo(jobCard.getOperatorUser()));
        info.setMechanicUser(UserMapper.toBasicInfo(jobCard.getMechanicUser()));
        info.setForemanUser(UserMapper.toBasicInfo(jobCard.getForemanUser()));
        info.setSupervisorUser(UserMapper.toBasicInfo(jobCard.getSupervisorUser()));
        info.setJobCardStatus(jobCard.getJobCardStatus().getStatus());
        info.setRiskAssessment(jobCard.isRiskAssessment());
        info.setLockOutProcedure(jobCard.isLockOutProcedure());
        info.setWheelNuts(jobCard.isWheelNuts());
        info.setOilLevel(jobCard.isOilLevel());
        info.setMachineGrease(jobCard.isMachineGrease());
        return info;
    }
}
