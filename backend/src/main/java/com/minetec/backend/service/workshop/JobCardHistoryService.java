package com.minetec.backend.service.workshop;

import com.minetec.backend.entity.workshop.JobCardHistory;
import com.minetec.backend.repository.workshop.JobCardHistoryRepository;
import com.minetec.backend.service.EntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Sinan
 */
@Service
@RequiredArgsConstructor
public class JobCardHistoryService extends EntityService<JobCardHistory, JobCardHistoryRepository> {
}
