package com.minetec.backend.repository;

import com.minetec.backend.entity.Order;
import com.minetec.backend.repository.projection.OrderListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Sinan
 */
@Repository
public interface OrderRepository extends BaseRepository<Order> {

    @Query("select o from Order o  where o.isActive = true ")
    Page<OrderListItemProjection> list(Pageable pageable);

    @Query("select o from Order o  where o.isActive = true and " +
        " o.requestDate between :requestStartDate and :requestEndDate and " +
        " o.status in ('PURCHASE_REQUEST') and " +
        " o.site.id = :siteId and " +
        " o.requestNumber like %:requestNumber% and " +
        " o.supplier.name like %:supplierName%  ")
    Page<OrderListItemProjection> filterByRequestDate(LocalDateTime requestStartDate, LocalDateTime requestEndDate,
                                                      Long siteId, String requestNumber, String supplierName,
                                                      Pageable pageable);


    @Query("select o from Order o  where o.isActive = true and " +
        " o.orderCreationDate between :orderCreationStartDate and :orderCreationEndDate and " +
        " o.site.id = :siteId and " +
        " o.orderNumber like %:orderNumber% and " +
        " o.supplier.name like %:supplierName%  and " +
        " o.status in ('ORDER', 'PARTIAL') ")
    Page<OrderListItemProjection> filterByOrderCreationDate(LocalDateTime orderCreationStartDate,
                                                            LocalDateTime orderCreationEndDate,
                                                            Long siteId, String orderNumber, String supplierName,
                                                            Pageable pageable);


    @Query("select o from Order o  where o.isActive = true and " +
        " o.invoiceDate between :invoiceStartDate and :invoiceEndDate and  " +
        " o.site.id = :siteId and " +
        " o.orderNumber like %:invoiceNumber% and " +
        " o.supplier.name like %:supplierName%  and " +
        " o.status in ('INVOICE') ")
    Page<OrderListItemProjection> filterByInvoiceDate(LocalDateTime invoiceStartDate, LocalDateTime invoiceEndDate,
                                                      Long siteId, String invoiceNumber, String supplierName,
                                                      Pageable pageable);

    @Query("select o from Order o  where o.isActive = true and o.status in ('REJECTED') and " +
        " o.site.id = :siteId ")
    Page<OrderListItemProjection> filterByReject(Long siteId, Pageable pageable);

    @Query("select count(o) from Order o  where o.isActive = true and " +
        "  o.requestDate between :requestStartDate and :requestEndDate and " +
        "  o.status = 'PURCHASE_REQUEST' ")
    Long filterByRequestCount(LocalDateTime requestStartDate, LocalDateTime requestEndDate);


    @Query("select count(o) from Order o  where o.isActive = true and " +
        "  o.orderCreationDate between :orderCreationStartDate and :orderCreationEndDate and " +
        "  o.status = 'ORDER' ")
    Long filterByOrderCreationCount(LocalDateTime orderCreationStartDate,
                                    LocalDateTime orderCreationEndDate);

    @Query("select count(o) from Order o  where o.isActive = true and " +
        " o.invoiceDate between :invoiceStartDate and :invoiceEndDate  and  " +
        " o.status = 'INVOICE' ")
    Long filterByInvoiceCount(LocalDateTime invoiceStartDate, LocalDateTime invoiceEndDate);


    @Query("select count(o) from Order o  where o.isActive = true and o.status = 'REJECT' ")
    Long filterByRejectCount();


    Page<OrderListItemProjection> findAll(@NotNull Specification specification, @NotNull Pageable pageable);
}
