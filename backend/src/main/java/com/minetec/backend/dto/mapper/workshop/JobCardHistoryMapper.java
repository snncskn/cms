package com.minetec.backend.dto.mapper.workshop;

import com.minetec.backend.dto.info.workshop.JobCardHistoryListInfo;
import com.minetec.backend.entity.workshop.JobCardHistory;
import com.minetec.backend.util.Utils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class JobCardHistoryMapper {
    /**
     * @param jobCardHistory
     * @return
     */
    public static JobCardHistoryListInfo toInfo(final JobCardHistory jobCardHistory) {
        var hist = new JobCardHistoryListInfo();
        hist.setCreatedAt(Utils.toString(jobCardHistory.getCreatedAt()));
        hist.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        hist.setDescription(jobCardHistory.getDescription());
        return hist;
    }

    /**
     * @param jobCardHistories
     * @return
     */
    public static List<JobCardHistoryListInfo> toInfos(final List<JobCardHistory> jobCardHistories) {
        var list = new ArrayList<JobCardHistoryListInfo>();
        jobCardHistories.forEach(jobCardHistory -> list.add(toInfo(jobCardHistory)));
        return list;
    }
}
