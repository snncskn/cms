package com.minetec.backend.repository;

import com.minetec.backend.entity.OrderHistory;
import com.minetec.backend.repository.projection.OrderHistoryListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

@Repository
public interface OrderHistoryRepository extends BaseRepository<OrderHistory> {

    @Query("select oh from OrderHistory oh ")
    Page<OrderHistoryListItemProjection> list(Pageable pageable);

    Page<OrderHistoryListItemProjection> findAll(@NotNull Specification specification, @NotNull Pageable pageable);
}
