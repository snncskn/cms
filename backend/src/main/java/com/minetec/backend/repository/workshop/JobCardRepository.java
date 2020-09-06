package com.minetec.backend.repository.workshop;

import com.minetec.backend.entity.workshop.JobCard;
import com.minetec.backend.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface JobCardRepository extends BaseRepository<JobCard> {

    @Query("select jc from JobCard jc where " +
        " jc.isActive = true and " +
        " jc.breakDownStartDate >= :breakDownStartDate and " +
        " (jc.breakDownEndDate is null or  jc.breakDownEndDate <= :breakDownEndDate) and " +
        " jc.vehicle.fleetNo like  %:fleetNumber%  and " +
        " COALESCE (jc.reportNumber, '')  like %:reportNumber% and  " +
        " jc.vehicle.site.description like %:site%  ")
    Page<JobCard> filter(LocalDateTime breakDownStartDate, LocalDateTime breakDownEndDate,
                         String fleetNumber, String reportNumber, String site, Pageable pageable);


    @Query("select jc from JobCard jc where " +
        " jc.isActive = true and " +
        " jc.breakDownStartDate >= :breakDownStartDate and " +
        " (jc.breakDownEndDate is null or  jc.breakDownEndDate <= :breakDownEndDate) and " +
        "  jc.vehicle.uuid = :vehicleUuid  ")
    Page<JobCard> filterByVehicleUuid(LocalDateTime breakDownStartDate,
                                      LocalDateTime breakDownEndDate,
                                      UUID vehicleUuid,
                                      Pageable pageable);

    /**
     * @param vehicleUuid
     * @return
     */
    @Query("select count(jc) from JobCard jc where jc.isActive = true and jc.vehicle.uuid = :vehicleUuid and " +
        " jc.jobCardStatus in ('ACTIVE_JOB_CARD', 'ACTIVE_BREAKDOWN_REPORT') ")
    Long findByJobCardStatusAndActiveAndVehicleUuid(UUID vehicleUuid);
}
