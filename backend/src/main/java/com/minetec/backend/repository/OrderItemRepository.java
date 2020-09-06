package com.minetec.backend.repository;

import com.minetec.backend.entity.Order;
import com.minetec.backend.entity.OrderItem;
import com.minetec.backend.repository.projection.OrderItemListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Sinan
 */
@Repository
public interface OrderItemRepository extends BaseRepository<OrderItem> {

    @Query("select oi from OrderItem oi  where oi.isActive = true ")
    Page<OrderItemListItemProjection> list(Pageable pageable);

    @Query("select oi from OrderItem oi  where oi.isActive = true and oi.order.id = ?1 ")
    List<OrderItem> findByOrderId(Long orderId);

    Page<OrderItemListItemProjection> findAll(@NotNull Specification specification, @NotNull Pageable pageable);

    @Query("select oi from OrderItem oi  where oi.isActive = true and oi.uuid = ?1 ")
    List<OrderItem> findByOrderItemUuid(UUID orderItemUuid);

    @Query("select oi.order from OrderItem oi  where oi.isActive = true and  oi.uuid = ?1 ")
    Order findByOrder(UUID orderItemUuid);

    @Query("select oi from OrderItem oi  where oi.isActive = true and " +
        " oi.item.uuid = :itemUuid and " +
        " oi.order.requestDate between :startDate and :endDate ")
    Page<OrderItem> filterBy(UUID itemUuid, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    @Query("select oi from OrderItem oi  where oi.isActive = true  and oi.order.site.id = :siteId and " +
        " oi.item.id = :itemId")
    List<OrderItem> findByOrderItemId(Long siteId, Long itemId);

}
