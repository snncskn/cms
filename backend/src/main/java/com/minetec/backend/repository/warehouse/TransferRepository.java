package com.minetec.backend.repository.warehouse;

import com.minetec.backend.entity.warehouse.Transfer;
import com.minetec.backend.repository.BaseRepository;
import com.minetec.backend.repository.projection.warehouse.TransferListItemProjection;
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
public interface TransferRepository extends BaseRepository<Transfer> {

    @Query("select o from Transfer o  where o.isActive = true ")
    Page<TransferListItemProjection> list(Pageable pageable);


    @Query("select o from Transfer o  where o.isActive = true and " +
        "  o.requestDate between :requestStartDate and :requestEndDate and " +
        "  o.transferNumber like %:transferNumber% and " +
        "  o.sourceSite.description like %:sourceSite% and " +
        "  o.targetSite.description like %:targetSite% and " +
        "  o.status = 'REQUEST' ")
    Page<TransferListItemProjection> filterByRequestDate(LocalDateTime requestStartDate, LocalDateTime requestEndDate,
                                                         String transferNumber, String sourceSite, String targetSite,
                                                         Pageable pageable);

    @Query("select o from Transfer o  where o.isActive = true and " +
        "  o.transferDate between :transferStartDate and :transferEndDate and " +
        "  o.transferNumber like %:transferNumber% and " +
        "  o.sourceSite.description like %:sourceSite% and " +
        "  o.targetSite.description like %:targetSite% and " +
        "  o.status in ('TRANSFER', 'WAITING_DELIVER') ")
    Page<TransferListItemProjection> filterByTransferDate(LocalDateTime transferStartDate,
                                                          LocalDateTime transferEndDate, String transferNumber,
                                                          String sourceSite, String targetSite, Pageable pageable);

    @Query("select o from Transfer o  where o.isActive = true and " +
        " o.deliverDate between :deliverStartDate and :deliverEndDate  and  " +
        " o.transferNumber like %:transferNumber% and " +
        " o.sourceSite.description like %:sourceSite% and " +
        " o.targetSite.description like %:targetSite% and " +
        " o.status in ('PARTIAL','WAITING_DELIVER','DELIVER') ")
    Page<TransferListItemProjection> filterByDeliverDate(LocalDateTime deliverStartDate, LocalDateTime deliverEndDate,
                                                         String transferNumber, String sourceSite, String targetSite,
                                                         Pageable pageable);


    @Query("select o from Transfer o  where o.isActive = true and " +
        " o.deliverDate between :deliverStartDate and :deliverEndDate and " +
        " o.transferNumber like %:transferNumber% and " +
        " o.sourceSite.description like %:sourceSite% and " +
        " o.targetSite.description like %:targetSite% and " +
        " o.status in ('REJECTED') ")
    Page<TransferListItemProjection> filterByRejectedDate(LocalDateTime deliverStartDate, LocalDateTime deliverEndDate,
                                                          String transferNumber, String sourceSite, String targetSite,
                                                          Pageable pageable);


    @Query("select count(o) from Transfer o  where o.isActive = true and " +
        "  o.requestDate between :requestStartDate and :requestEndDate and " +
        "  o.status = 'REQUEST' ")
    Long filterByRequestCount(LocalDateTime requestStartDate,
                              LocalDateTime requestEndDate);

    @Query("select count(o) from Transfer o  where o.isActive = true and " +
        "  o.transferDate between :transferStartDate and :transferEndDate and " +
        "  o.status in ('TRANSFER', 'PARTIAL') ")
    Long filterByTransferCount(LocalDateTime transferStartDate,
                               LocalDateTime transferEndDate);

    @Query("select count(o) from Transfer o  where o.isActive = true and " +
        " o.deliverDate between :deliverStartDate and :deliverEndDate  and  o.status in ('WAITING_DELIVER','DELIVER') ")
    Long filterByDeliverCount(LocalDateTime deliverStartDate, LocalDateTime deliverEndDate);

    @Query("select count(o) from Transfer o  where o.isActive = true and " +
        " o.deliverDate between :deliverStartDate and :deliverEndDate  and  o.status in ('REJECTED') ")
    Long filterByRejectedCount(LocalDateTime deliverStartDate, LocalDateTime deliverEndDate);

    Page<TransferListItemProjection> findAll(@NotNull Specification specification, @NotNull Pageable pageable);
}
