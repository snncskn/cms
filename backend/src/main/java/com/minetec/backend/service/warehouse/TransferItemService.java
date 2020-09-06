package com.minetec.backend.service.warehouse;

import com.minetec.backend.dto.form.MessageCreateUpdateForm;
import com.minetec.backend.dto.form.warehouse.TransferApproveForm;
import com.minetec.backend.dto.form.warehouse.TransferItemCreateUpdateForm;
import com.minetec.backend.dto.info.MessageInfo;
import com.minetec.backend.dto.info.warehouse.TransferItemInfo;
import com.minetec.backend.dto.mapper.TransferItemMapper;
import com.minetec.backend.entity.warehouse.Transfer;
import com.minetec.backend.entity.warehouse.TransferItem;
import com.minetec.backend.repository.projection.warehouse.TransferItemListItemProjection;
import com.minetec.backend.repository.warehouse.TransferItemRepository;
import com.minetec.backend.service.EntityService;
import com.minetec.backend.service.ItemService;
import com.minetec.backend.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

import static com.minetec.backend.entity.AbstractEntity.CURRENCY_SCALE;
import static com.minetec.backend.util.Utils.toBigDecimal;
import static org.springframework.data.jpa.domain.Specification.where;

/**
 * @author Sinan
 */
@Service
@RequiredArgsConstructor
public class TransferItemService extends EntityService<TransferItem, TransferItemRepository> {

    private final ItemService itemService;

    public Page<TransferItemListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * @param transfer
     * @param transferItemCreateUpdateForm
     * @return
     */
    public TransferItemInfo create(final Transfer transfer,
                                   final TransferItemCreateUpdateForm transferItemCreateUpdateForm) {
        var transferItem = new TransferItem();
        if (!Utils.isEmpty(transferItemCreateUpdateForm.getUuid())) {
            return update(transferItemCreateUpdateForm.getUuid(), transferItemCreateUpdateForm);
        }
        this.initEntity(transferItem, transferItemCreateUpdateForm);
        transferItem.setTransfer(transfer);
        transferItem.setItem(itemService.findByUuid(transferItemCreateUpdateForm.getItemUuid()));
        transferItem.setApproveQuantity(toBigDecimal(transferItemCreateUpdateForm.getQuantity()));
        this.persist(transferItem);
        return TransferItemMapper.toInfo(transferItem);
    }

    /**
     * @param transferItemUuid
     * @param transferItemCreateUpdateForm
     * @return
     */
    public TransferItemInfo update(final UUID transferItemUuid,
                                   final TransferItemCreateUpdateForm transferItemCreateUpdateForm) {
        var transferItem = this.findByUuid(transferItemUuid);
        this.initEntity(transferItem, transferItemCreateUpdateForm);
        this.persist(transferItem);
        return TransferItemMapper.toInfo(transferItem);
    }

    /**
     * @param entity
     * @param transferItemCreateUpdateForm
     */
    private void initEntity(final TransferItem entity,
                            final TransferItemCreateUpdateForm transferItemCreateUpdateForm) {
        entity.setQuantity(toBigDecimal(transferItemCreateUpdateForm.getQuantity()));
        entity.setDescription(transferItemCreateUpdateForm.getDescription());
    }

    /**
     * @param uuid
     * @return
     */
    public TransferItemInfo find(final UUID uuid) {
        return TransferItemMapper.toInfo(this.findByUuid(uuid));
    }

    /**
     * @param uuid
     */
    public void softDelete(final UUID uuid) {
        var entity = this.findByUuid(uuid);
        entity.setActive(false);
        this.persist(entity);
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<TransferItemListItemProjection> findBy(final String value, final Pageable pageable) {
        return this.getRepository().findAll(this.getFilter(value), pageable);
    }


    /**
     * @param filter
     * @return
     */
    public Specification<TransferItemListItemProjection> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("description", filter)
                .or(contains("barcode", filter)))
            .and(equals("isActive", true))
            .toPredicate(root, query, cb);
    }

    /**
     * @param transferItem
     * @param form
     * @return
     */
    public MessageInfo createTransferItemMessage(final TransferItem transferItem,
                                                 final MessageCreateUpdateForm form) {
        return null;
    }

    /**
     * @param transferItem
     * @param form
     * @return
     */
    public boolean createApproveQuantity(final TransferItem transferItem,
                                         final TransferApproveForm form) {
        var newTransferItem = this.findByUuid(transferItem.getUuid());
        if (!newTransferItem.getQuantity().equals(
            form.getApproveQuantity().setScale(CURRENCY_SCALE, RoundingMode.HALF_DOWN))) {
            newTransferItem.setApprove(false);
        } else {
            newTransferItem.setApprove(true);
        }
        newTransferItem.setApproveQuantity(form.getApproveQuantity());
        this.persist(newTransferItem);
        return true;
    }

    /**
     * @param pageable
     * @return
     */
    public Page<MessageInfo> findByTransferItemId(@NotNull final UUID transferItemUuid,
                                                  final Pageable pageable) {
        var transferItem = this.findByUuid(transferItemUuid);
        return null;
    }

    /**
     * @param transferItemUuid
     * @return
     */
    public List<TransferItem> findByTransferItemUuid(@NotNull final UUID transferItemUuid) {
        return this.getRepository().findByTransferItemUuid(transferItemUuid);
    }

    /**
     * @param transferItemUuid
     * @return
     */
    public Transfer findByTransferByTransferItemUuid(@NotNull final UUID transferItemUuid) {
        return this.getRepository().findByTransfer(transferItemUuid);
    }
}
