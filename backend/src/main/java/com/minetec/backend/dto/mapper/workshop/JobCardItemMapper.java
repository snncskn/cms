package com.minetec.backend.dto.mapper.workshop;

import com.minetec.backend.dto.info.workshop.JobCardItemDeliveredListInfo;
import com.minetec.backend.dto.info.workshop.JobCardItemInfo;
import com.minetec.backend.dto.mapper.ItemMapper;
import com.minetec.backend.dto.mapper.UserMapper;
import com.minetec.backend.entity.StockHistory;
import com.minetec.backend.entity.User;
import com.minetec.backend.entity.workshop.JobCardItem;
import com.minetec.backend.util.Utils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class JobCardItemMapper {

    public static JobCardItemInfo toInfo(@NotNull final JobCardItem jobCardItem) {

        var jobCardItemInfo = new JobCardItemInfo();
        jobCardItemInfo.setUuid(jobCardItem.getUuid());
        jobCardItemInfo.setItemInfo(ItemMapper.toInfo(jobCardItem.getItem()));
        jobCardItemInfo.setJobCardItemStatus(jobCardItem.getJobCardItemStatus().getStatus());
        jobCardItemInfo.setReceivedUserInfo(UserMapper.toBasicInfo(jobCardItem.getReceivedUser()));
        jobCardItemInfo.setRequestUserInfo(UserMapper.toBasicInfo(jobCardItem.getRequestUser()));
        jobCardItemInfo.setDeliveredUserInfo(UserMapper.toBasicInfo(jobCardItem.getDeliveredUser()));
        jobCardItemInfo.setRequestDate(Utils.toString(jobCardItem.getRequestDate()));
        jobCardItemInfo.setDeliveredDate(Utils.toString(jobCardItem.getDeliveredDate()));
        jobCardItemInfo.setDescription(jobCardItem.getItem().getItemDescription());
        jobCardItemInfo.setJobCardItemId(jobCardItem.getId());
        jobCardItemInfo.setSourceSiteId(jobCardItem.getJobCard().getVehicle().getSite().getId());

        jobCardItemInfo.setQuantity(jobCardItem.getQuantity());
        jobCardItemInfo.setApproveQuantity(jobCardItem.getApproveQuantity());
        jobCardItemInfo.setDeliveredQuantity(jobCardItem.getDeliveredQuantity());
        jobCardItemInfo.setAvailableQuantity(jobCardItem.getAvailableQuantity());

        if (Utils.isEmpty(jobCardItem.getApproveQuantity())) {
            jobCardItemInfo.setApproveQuantity(BigDecimal.ZERO);
        }

        if (Utils.isEmpty(jobCardItem.getAvailableQuantity())) {
            jobCardItemInfo.setAvailableQuantity(BigDecimal.ZERO);
        }

        if (Utils.isEmpty(jobCardItem.getDeliveredQuantity())) {
            jobCardItemInfo.setDeliveredQuantity(BigDecimal.ZERO);
        }

        jobCardItemInfo.setRemainingQuantity(
            jobCardItemInfo.getQuantity().subtract(jobCardItemInfo.getDeliveredQuantity())
        );

        return jobCardItemInfo;
    }


    public static JobCardItemDeliveredListInfo toDeliveredInfo(@NotNull final JobCardItem jobCardItem) {

        var deliveredListInfo = new JobCardItemDeliveredListInfo();
        deliveredListInfo.setJobCardItemUuid(jobCardItem.getUuid());
        deliveredListInfo.setReceivedUserInfo(UserMapper.toBasicInfo(jobCardItem.getReceivedUser()));
        deliveredListInfo.setDeliveredUserInfo(UserMapper.toBasicInfo(jobCardItem.getDeliveredUser()));
        deliveredListInfo.setDeliveredDate(Utils.toString(jobCardItem.getDeliveredDate()));
        deliveredListInfo.setJobCardItemId(jobCardItem.getId());
        deliveredListInfo.setSourceSiteId(jobCardItem.getJobCard().getVehicle().getSite().getId());
        return deliveredListInfo;
    }


    public static JobCardItemDeliveredListInfo toDeliveredInfo(@NotNull final StockHistory stockHistory) {

        var deliveredListInfo = new JobCardItemDeliveredListInfo();
        deliveredListInfo.setJobCardItemUuid(stockHistory.getUuid());
        deliveredListInfo.setReceivedUserInfo(UserMapper.toBasicInfo(stockHistory.getReceivedUser()));
        deliveredListInfo.setJobCardItemId(stockHistory.getId());
        deliveredListInfo.setSourceSiteId(stockHistory.getSourceSite().getId());
        deliveredListInfo.setDeliveredQuantity(stockHistory.getQuantity());
        deliveredListInfo.setDeliveredUserInfo(
            UserMapper.toBasicInfo((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
        deliveredListInfo.setDeliveredDate(Utils.toString(stockHistory.getCreatedAt()));
        return deliveredListInfo;
    }


}
