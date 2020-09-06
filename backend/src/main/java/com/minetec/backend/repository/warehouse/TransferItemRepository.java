package com.minetec.backend.repository.warehouse;

import com.minetec.backend.entity.warehouse.Transfer;
import com.minetec.backend.entity.warehouse.TransferItem;
import com.minetec.backend.repository.BaseRepository;
import com.minetec.backend.repository.projection.warehouse.TransferItemListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * @author Sinan
 */
@Repository
public interface TransferItemRepository extends BaseRepository<TransferItem> {

    @Query("select oi from TransferItem oi  where oi.isActive = true ")
    Page<TransferItemListItemProjection> list(Pageable pageable);

    @Query("select oi from TransferItem oi  where oi.isActive = true and oi.transfer.id = ?1 ")
    List<TransferItem> findByTransferId(Long transferId);

    Page<TransferItemListItemProjection> findAll(@NotNull Specification specification, @NotNull Pageable pageable);

    @Query("select oi from TransferItem oi  where oi.isActive = true and oi.uuid = ?1 ")
    List<TransferItem> findByTransferItemUuid(UUID transferItemUuid);

    @Query("select oi.transfer from TransferItem oi  where oi.isActive = true and  oi.uuid = ?1 ")
    Transfer findByTransfer(UUID transferItemUuid);

}
