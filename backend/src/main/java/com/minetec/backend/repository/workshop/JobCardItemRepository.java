package com.minetec.backend.repository.workshop;

import com.minetec.backend.entity.workshop.JobCardItem;
import com.minetec.backend.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface JobCardItemRepository extends BaseRepository<JobCardItem> {

    Page<JobCardItem> findByJobCardUuid(UUID jobCardUuid, Pageable pageable);

    @Query("select jci from JobCardItem jci where " +
        " jci.isActive = true and " +
        " jci.requestDate >= :requestStartDate and " +
        " (jci.requestDate is null or  jci.requestDate <= :requestEndDate) and " +
        " jci.jobCard.vehicle.site.uuid = :siteUuid  and " +
        " jci.jobCard.reportNumber  like %:reportNumber% and  " +
        " jci.jobCard.vehicle.fleetNo like  %:fleetNumber%  and " +
        " jci.item.storePartNumber like %:storePartNumber%  and" +
        " jci.item.itemDescription like %:itemDescription% and " +
        " ( jci.requestUser.firstName like %:requestUser% or " +
        "   jci.requestUser.lastName like %:requestUser% )" +
        " order by jci.jobCardItemStatus desc ")
    Page<JobCardItem> filter(LocalDateTime requestStartDate, LocalDateTime requestEndDate, UUID siteUuid,
                             String requestUser, String reportNumber, String fleetNumber, String storePartNumber,
                             String itemDescription, Pageable pageable);


    @Query("select jci from JobCardItem jci where jci.isActive = true and jci.uuid =  :jobCardItemUuid ")
    Page<JobCardItem> findByDelivered(@NotNull UUID jobCardItemUuid, Pageable pageable);
}
