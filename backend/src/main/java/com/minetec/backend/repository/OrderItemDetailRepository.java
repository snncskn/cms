package com.minetec.backend.repository;

import com.minetec.backend.entity.OrderItemDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemDetailRepository extends BaseRepository<OrderItemDetail> {
    @Query("select oid from OrderItemDetail oid  where oid.orderItem.id = :orderItemId ")
    Page<OrderItemDetail> list(Long orderItemId, Pageable pageable);
}
