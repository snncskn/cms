package com.minetec.backend.repository.workshop;

import com.minetec.backend.entity.workshop.JobCardHistory;
import com.minetec.backend.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JobCardHistoryRepository extends BaseRepository<JobCardHistory> {
    Page<JobCardHistory> findJobCardHistoryByJobCardUuid(UUID jobCardUuid, Pageable pageable);

    @Query("select jch from JobCardHistory jch where jch.jobCard.uuid = :jobCardUuid")
    Long countJobCardHistoryByJobCardUuid(UUID jobCardUuid);
}
