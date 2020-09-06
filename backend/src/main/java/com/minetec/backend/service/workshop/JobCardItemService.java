package com.minetec.backend.service.workshop;

import com.minetec.backend.dto.form.workshop.JobCardItemCreateForm;
import com.minetec.backend.dto.info.workshop.JobCardItemInfo;
import com.minetec.backend.dto.mapper.workshop.JobCardItemMapper;
import com.minetec.backend.entity.Item;
import com.minetec.backend.entity.workshop.JobCard;
import com.minetec.backend.entity.workshop.JobCardItem;
import com.minetec.backend.repository.workshop.JobCardItemRepository;
import com.minetec.backend.service.EntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * @author Sinan
 */
@Service
@RequiredArgsConstructor
public class JobCardItemService extends EntityService<JobCardItem, JobCardItemRepository> {

    /**
     * @param form
     * @return
     */
    public JobCardItemInfo create(@NotNull final JobCard jobCard,
                                  @NotNull final Item item,
                                  @NotNull final JobCardItemCreateForm form) {
        var jobCardItem = new JobCardItem();
        jobCardItem.setJobCard(jobCard);
        jobCardItem.setItem(item);
        jobCardItem.setQuantity(form.getQuantity());
        jobCardItem.setDescription(form.getDescription());
      /*  jobCardItem.setJobCardItemStatus(form.getJobCardItemStatus());
        jobCardItem.setApproveQuantity(form.getApproveQuantity());
        jobCardItem.setReceivedUser(userService.findByUuid(form.getReceivedUserUuid()));
        jobCardItem.setRequestUser(userService.findByUuid(form.getRequestUserUuid()));
        jobCardItem.setRequestDate(Utils.toLocalDate(form.getRequestDate()));
        jobCardItem.setDeliveredUser(userService.findByUuid(form.getDeliveredUserUuid()));
        jobCardItem.setDeliveredDate(Utils.toLocalDate(form.getDeliveredDate())); */
        var newJobCardItem = this.persist(jobCardItem);
        return JobCardItemMapper.toInfo(newJobCardItem);
    }

}
