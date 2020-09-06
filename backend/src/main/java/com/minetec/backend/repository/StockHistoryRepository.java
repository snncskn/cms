package com.minetec.backend.repository;

import com.minetec.backend.entity.StockHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Sinan
 */
@Repository
public interface StockHistoryRepository extends BaseRepository<StockHistory> {

    @Query("select sum(sh.quantity) from StockHistory sh " +
        " where sh.stockType = 'OUTPUT' and  sh.sourceSite.id = :sourceSiteId and sh.jobCardItem.id = :jobCardItemId ")
    BigDecimal sumOutputQuantity(Long sourceSiteId, Long jobCardItemId);

    @Query("select sh from StockHistory sh " +
        " where sh.stockType = 'OUTPUT' and  sh.sourceSite.id = :sourceSiteId and sh.jobCardItem.id = :jobCardItemId ")
    Page<StockHistory> findByOutputQuantity(Long sourceSiteId, Long jobCardItemId, Pageable pageable);

    /**
     *
     * @param startDate
     * @param endDate
     * @param stockCode
     * @param itemDescription
     * @param sourceSite
     * @param pageable
     * @return
     */
    @Query("select sh from StockHistory sh where " +
        " sh.createdAt between  :startDate and :endDate and " +
        " sh.item.storePartNumber like %:stockCode%  and " +
        " sh.item.itemDescription  like %:itemDescription%  and " +
        " sh.sourceSite.description like %:sourceSite% ")
    Page<StockHistory> filterBy(LocalDateTime startDate, LocalDateTime endDate, String stockCode,
                                String itemDescription, String sourceSite, Pageable pageable);

    @Query("select sh from StockHistory sh where sh.stockType = 'OUTPUT' and " +
        " sh.sourceSite.id = :sourceSiteId and sh.jobCardItem.id = :jobCardItemId ")
    Page<StockHistory> findByJobCardItem(Long sourceSiteId, Long jobCardItemId, Pageable pageable);

    @Query("select sh from StockHistory sh where sh.stockType = 'INPUT' and sh.orderItem.isActive = true and " +
        " sh.sourceSite.id = :sourceSiteId and sh.orderItem.id = :orderItemId ")
    List<StockHistory> findByOrderItem(@NotNull Long orderItemId);

    @Query("select sum(sh.quantity) from StockHistory sh where sh.stockType = 'INPUT' and " +
        " sh.orderItem.isActive = true and sh.sourceSite.id = :sourceSiteId and sh.orderItem.id = :orderItemId ")
    BigDecimal sumInputQuantity(@NotNull final Long sourceSiteId, @NotNull Long orderItemId);

    @Query("select sh from StockHistory sh where sh.stockType = 'OUTPUT' and " +
        " sh.sourceSite.id = :sourceSiteId and sh.jobCardItem.id = :jobCardItemId ")
    List<StockHistory> findByJobCardItem(Long sourceSiteId, Long jobCardItemId);

    @Query("select sh from StockHistory sh where sh.jobCardItem.jobCard.vehicle.id = :vehicleId and " +
        " sh.jobCardItem.deliveredDate between :deliveredStartDate and :deliveredEndDate ")
    Page<StockHistory> findByVehicleId(Long vehicleId, LocalDateTime deliveredStartDate,
                                       LocalDateTime deliveredEndDate, Pageable pageable);

}
