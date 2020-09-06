package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.workshop.RequestListInfo;
import com.minetec.backend.entity.workshop.JobCardItem;
import com.minetec.backend.util.Utils;

import java.time.LocalDateTime;

public class RequestListMapper {
    public static RequestListInfo toMap(final JobCardItem jobCardItem) {
        var info = new RequestListInfo();
        info.setUuid(jobCardItem.getUuid());
        info.setReportNumber(jobCardItem.getJobCard().getReportNumber());
        info.setFleetNumber(jobCardItem.getJobCard().getVehicle().getFleetNo());
        final var requestUser = jobCardItem.getRequestUser();
        var sb = new StringBuilder(requestUser.getFirstName()).append(" ").append(requestUser.getLastName());
        info.setRequestUser(sb.toString());
        info.setRequestDate(Utils.toString(jobCardItem.getRequestDate()));
        info.setDeliveredDate(Utils.toString(jobCardItem.getDeliveredDate()));
        info.setDeliveryTime(Utils.formatterDaysAndHour(jobCardItem.getRequestDate(), LocalDateTime.now()));
        info.setStockCode(jobCardItem.getItem().getStorePartNumber());
        info.setDescription(jobCardItem.getItem().getItemDescription());
        info.setJobCardItemStatus(jobCardItem.getJobCardItemStatus().getStatus());
        info.setBarcode(jobCardItem.getItem().getBarcode());
        info.setItemDescription(jobCardItem.getItem().getItemDescription());

        info.setApproveQuantity(jobCardItem.getApproveQuantity());
        info.setRequestedQuantity(jobCardItem.getQuantity());
        info.setDeliveredQuantity(jobCardItem.getDeliveredQuantity());
        info.setAvailableQuantity(jobCardItem.getAvailableQuantity());
        info.setRemainingQuantity(jobCardItem.getQuantity().subtract(jobCardItem.getDeliveredQuantity()));

        info.setSiteId(jobCardItem.getJobCard().getVehicle().getSite().getId());
        info.setItemId(jobCardItem.getItem().getId());

        if (!Utils.isEmpty(jobCardItem.getReceivedUser())) {
            info.setReceivedUserUuid(jobCardItem.getReceivedUser().getUuid());
        }

        info.setJobCardUuid(jobCardItem.getJobCard().getUuid());
        return info;
    }
}
