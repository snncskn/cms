package com.minetec.backend.repository.warehouse;

import com.minetec.backend.entity.warehouse.TransferHistory;
import com.minetec.backend.repository.BaseRepository;
import com.minetec.backend.repository.projection.warehouse.TransferHistoryListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface TransferHistoryRepository extends BaseRepository<TransferHistory> {

    @Query("select th from TransferHistory th ")
    Page<TransferHistoryListItemProjection> list(Pageable pageable);

    Page<TransferHistoryListItemProjection> findAll(@NotNull Specification specification, @NotNull Pageable pageable);
}
