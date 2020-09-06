package com.minetec.backend.service.warehouse;

import com.minetec.backend.dto.enums.TransferStatus;
import com.minetec.backend.dto.filter.warehouse.TransferFilterForm;
import com.minetec.backend.dto.form.ApproveRejectForm;
import com.minetec.backend.dto.form.DateFilterForm;
import com.minetec.backend.dto.form.MessageCreateUpdateForm;
import com.minetec.backend.dto.form.warehouse.TransferApproveForm;
import com.minetec.backend.dto.form.warehouse.TransferCreateUpdateForm;
import com.minetec.backend.dto.form.warehouse.TransferDeliverForm;
import com.minetec.backend.dto.form.warehouse.TransferItemCreateUpdateForm;
import com.minetec.backend.dto.info.BasicUserInfo;
import com.minetec.backend.dto.info.MessageInfo;
import com.minetec.backend.dto.info.warehouse.TransferInfo;
import com.minetec.backend.dto.info.warehouse.TransferItemInfo;
import com.minetec.backend.dto.info.warehouse.TransferListResponseInfo;
import com.minetec.backend.dto.mapper.TransferItemMapper;
import com.minetec.backend.dto.mapper.warehouse.TransferMapper;
import com.minetec.backend.entity.warehouse.Transfer;
import com.minetec.backend.entity.warehouse.TransferItem;
import com.minetec.backend.repository.projection.warehouse.TransferListItemProjection;
import com.minetec.backend.repository.warehouse.TransferRepository;
import com.minetec.backend.service.EntityService;
import com.minetec.backend.service.ItemService;
import com.minetec.backend.service.SiteService;
import com.minetec.backend.service.StockHistoryService;
import com.minetec.backend.service.StockService;
import com.minetec.backend.util.Utils;
import com.minetec.backend.util.excel_generator.TransferListExcel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.minetec.backend.dto.enums.TransferStatus.DELIVER;
import static com.minetec.backend.dto.enums.TransferStatus.REJECTED;
import static com.minetec.backend.dto.enums.TransferStatus.WAITING_DELIVER;
import static org.springframework.data.jpa.domain.Specification.where;

/**
 * @author Sinan
 */
@Service
@RequiredArgsConstructor
public class TransferService extends EntityService<Transfer, TransferRepository> {

    private static final int NUMBER_LENGTH = 10;

    private final ItemService itemService;
    private final SiteService siteService;
    private final TransferItemService transferItemService;
    private final TransferHistoryService transferHistoryService;
    private final JdbcTemplate jdbcTemplate;
    private final StockService stockService;
    private final StockHistoryService stockHistoryService;

    /**
     * @param pageable
     * @return
     */
    public Page<TransferListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * @param form
     * @param pageable
     * @return
     */
    public TransferListResponseInfo filter(@NotNull final TransferFilterForm form, final Pageable pageable) {

        var responseInfo = new TransferListResponseInfo();

        LocalDateTime requestStartDate = null, requestEndDate = null,
            transferStartDate = null, transferEndDate = null,
            deliverStartDate = null, deliverEndDate = null;

        Page<TransferListItemProjection> list = null;

        switch (TransferStatus.valueOf(form.getStatus())) {
            case REQUEST:
                requestStartDate = Utils.toLocalDate(form.getStartDate());
                requestEndDate = Utils.toLocalDate(form.getEndDate());
                list = getRepository().filterByRequestDate(requestStartDate, requestEndDate,
                    form.getTransferNumber(), form.getSourceSite(), form.getTargetSite(), pageable);
                responseInfo.setRequestCounts(list.getTotalElements());
                break;
            case TRANSFER:
                transferStartDate = Utils.toLocalDate(form.getStartDate());
                transferEndDate = Utils.toLocalDate(form.getEndDate());
                list = getRepository().filterByTransferDate(transferStartDate, transferEndDate,
                    form.getTransferNumber(), form.getSourceSite(), form.getTargetSite(), pageable);
                responseInfo.setTransferCounts(list.getTotalElements());
                break;
            case DELIVER:
                deliverStartDate = Utils.toLocalDate(form.getStartDate());
                deliverEndDate = Utils.toLocalDate(form.getEndDate());
                list = getRepository().filterByDeliverDate(deliverStartDate, deliverEndDate,
                    form.getTransferNumber(), form.getSourceSite(), form.getTargetSite(), pageable);
                responseInfo.setDeliverCounts(list.getTotalElements());
                break;
            case REJECTED:
                deliverStartDate = Utils.toLocalDate(form.getStartDate());
                deliverEndDate = Utils.toLocalDate(form.getEndDate());
                list = getRepository().filterByRejectedDate(deliverStartDate, deliverEndDate,
                    form.getTransferNumber(), form.getSourceSite(), form.getTargetSite(), pageable);
                responseInfo.setRejectCounts(list.getTotalElements());
                break;
            default:
                break;
        }

        responseInfo.setTransferListItemProjections(list);

        return responseInfo;
    }

    /**
     * @param transferCreateUpdateForm
     * @return
     */
    public TransferInfo create(final TransferCreateUpdateForm transferCreateUpdateForm) {
        var entity = new Transfer();
        var newSeq = String.valueOf(
            jdbcTemplate.queryForObject("SELECT nextval('TRANSFER_NUMBER_SEQ')", Long.class));
        entity.setTransferNumber("TR ".concat(StringUtils.leftPad(newSeq, NUMBER_LENGTH, "0")));
        entity.setRequestDate(LocalDateTime.now());
        entity.setSourceOwner(this.getCurrentUser());
        entity.setTargetOwner(this.getCurrentUser());
        this.initEntity(transferCreateUpdateForm, entity);
        var newEntity = this.persist(entity);
        return TransferMapper.toInfo(newEntity);
    }

    /**
     * @param uuid
     * @param transferCreateUpdateForm
     * @return
     */
    public TransferInfo update(final UUID uuid, final TransferCreateUpdateForm transferCreateUpdateForm) {
        var entity = this.findByUuid(uuid);
        this.initEntity(transferCreateUpdateForm, entity);
        var newEntity = this.persist(entity);
        return TransferMapper.toInfo(newEntity);
    }

    /**
     * @param transferCreateUpdateForm
     * @param entity
     */
    private void initEntity(final TransferCreateUpdateForm transferCreateUpdateForm, final Transfer entity) {
        entity.setSourceSite(siteService.findByUuid(transferCreateUpdateForm.getSourceSiteUuid()));
        entity.setTargetSite(siteService.findByUuid(transferCreateUpdateForm.getTargetSiteUuid()));
        entity.setStatus(TransferStatus.valueOf(transferCreateUpdateForm.getStatus()));
    }

    /**
     * @param uuid
     * @return
     */
    public TransferInfo find(final UUID uuid) {
        var transfer = this.findByUuid(uuid);
        var transferInfo = TransferMapper.toInfo(transfer);
        var transferItems = transferItemService.getRepository().findByTransferId(transfer.getId());
        var tmpItems = new ArrayList<TransferItemInfo>();

        transferItems.forEach(item -> {
            var transferMap = TransferItemMapper.toInfo(item);
            if (TransferStatus.TRANSFER.equals(transfer.getStatus())) {
                transferMap.setApproveQuantity(item.getQuantity());
            }
            tmpItems.add(transferMap);
        });

        transferInfo.setTransferItemInfos(tmpItems);
        transferInfo.setBasicUserInfo(basicInfoMap(transfer));
        return transferInfo;
    }

    /**
     * @param transfer
     * @return
     */
    private BasicUserInfo basicInfoMap(final Transfer transfer) {
        var basicInfo = new BasicUserInfo();
        basicInfo.setCreateDate(Utils.toString(transfer.getCreatedAt()));
        basicInfo.setFullName(SecurityContextHolder.getContext().getAuthentication().getName());
        return basicInfo;
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
     * @param transferItemCreateUpdateForm
     * @return
     */
    public TransferItemInfo createTransferItem(final TransferItemCreateUpdateForm transferItemCreateUpdateForm) {
        var transfer = this.findByUuid(transferItemCreateUpdateForm.getTransferUuid());
        return transferItemService.create(transfer, transferItemCreateUpdateForm);
    }

    /**
     * @param transferItemUuid
     * @return
     */
    public TransferItemInfo findTransferItem(@NotNull final UUID transferItemUuid) {
        var transferItem = transferItemService.findByUuid(transferItemUuid);
        return TransferItemMapper.toInfo(transferItem);
    }

    /**
     * @param transferItemUuid
     */
    public void softDeleteTransferItem(@NotNull final UUID transferItemUuid) {
        transferItemService.softDelete(transferItemUuid);
    }

    /**
     * approveReject
     *
     * @return
     */
    public boolean approveReject(@NotNull final ApproveRejectForm approveRejectForm) {

        var transfer = this.findByUuid(approveRejectForm.getTransferUuid());
        transfer.setStatus(TransferStatus.valueOf(approveRejectForm.getStatus()));
        transfer.setRejectionReason(approveRejectForm.getRejectionReason());

        if (WAITING_DELIVER.equals(transfer.getStatus())) {
            transfer.setTransferDate(LocalDateTime.now());
            transfer.setDeliverDate(LocalDateTime.now());
            transfer.setStatus(WAITING_DELIVER);
        }

        if (DELIVER.equals(transfer.getStatus())) {
            transfer.setDeliverDate(LocalDateTime.now());
            transfer.setStatus(DELIVER);
        }

        if (REJECTED.equals(transfer.getStatus())) {
            transfer.setDeliverDate(LocalDateTime.now());
            transfer.setRejectionReason(approveRejectForm.getRejectionReason());
        }

        var newTransfer = this.persist(transfer);

        if (DELIVER.equals(transfer.getStatus())) {
            stockService.createTransfer(newTransfer.getTransferItems());
            stockHistoryService.createTransferItems(newTransfer.getTransferItems());
        }

        transferHistoryService.create(newTransfer);

        return true;
    }

    /**
     * @param transferItemUuid
     * @param transferItemCreateUpdateForm
     * @return
     */
    public Object updateTransferItem(final UUID transferItemUuid,
                                     final TransferItemCreateUpdateForm transferItemCreateUpdateForm) {
        return transferItemService.update(transferItemUuid, transferItemCreateUpdateForm);
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<TransferListItemProjection> findBy(final String value, final Pageable pageable) {
        return this.getRepository().findAll(this.getFilter(value), pageable);
    }

    /**
     * @param filter
     * @return
     */
    private Specification<TransferListItemProjection> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("transferNumber", filter))
            .and(equals("isActive", true))
            .toPredicate(root, query, cb);
    }

    /**
     * @param form
     * @return
     */
    public TransferStatus createApproveQuantity(final TransferApproveForm form) {
        final var transferItem = transferItemService.findByUuid(form.getTransferItemUuid());
        var result = transferItemService.createApproveQuantity(transferItem, form);

        final var transfer = transferItemService.findByTransferByTransferItemUuid(form.getTransferItemUuid());

        final var transferItems = transfer.getTransferItems();

        TransferStatus transferStatus = WAITING_DELIVER;

        if (result) {
            for (TransferItem item : transferItems) {
                if (!item.isApprove()) {
                    transferStatus = TransferStatus.PARTIAL;
                    break;
                }
            }
        }
        transfer.setStatus(transferStatus);
        transfer.setTransferDate(LocalDateTime.now());
        this.persist(transfer);
        return transferStatus;
    }

    /**
     * @param pageable
     * @return
     */
    public Page<MessageInfo> findByTransferItemId(final UUID transferItemUuid,
                                                  final Pageable pageable) {
        return transferItemService.findByTransferItemId(transferItemUuid, pageable);
    }

    /**
     * @param transferUuid
     * @param form
     * @return
     */
    public boolean updateDeliverInfo(final UUID transferUuid, final TransferDeliverForm form) {
        final var transfer = this.findByUuid(transferUuid);
        if (DELIVER.equals(transfer.getStatus())) {
            //transfer.setDate(Utils.toLocalDate(form.getTransferDate()));
            //transfer.setInvoiceNumber(form.getTransferNumber());
            this.persist(transfer);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param transferItemUuid
     * @return
     */
    public Transfer findTransferByTransferItemId(final UUID transferItemUuid) {
        return transferItemService.getRepository().findByTransfer(transferItemUuid);
    }

    /**
     * @param form
     * @return
     */
    public MessageInfo createTransferItemMessage(final MessageCreateUpdateForm form) {
        var orderItem = transferItemService.findByUuid(form.getOrderItemUuid());
        return transferItemService.createTransferItemMessage(orderItem, form);
    }

    /**
     * @param form
     * @return
     */
    public long transferCounts(@NotNull final DateFilterForm form) {
        LocalDateTime startDate = Utils.toLocalDate(form.getStartDate());
        LocalDateTime endDate = Utils.toLocalDate(form.getEndDate());
        Long count = 0L;
        switch (TransferStatus.valueOf(form.getStatus())) {
            case REQUEST:
                count = getRepository().filterByRequestCount(startDate, endDate);
                break;
            case TRANSFER:
                count = getRepository().filterByTransferCount(startDate, endDate);
                break;
            case DELIVER:
                count = getRepository().filterByDeliverCount(startDate, endDate);
                break;
            case REJECTED:
                count = getRepository().filterByRejectedCount(startDate, endDate);
                break;
            default:
                break;
        }
        return count;
    }

    /**
     * @param siteUuid
     * @param itemUuid
     * @return
     */
    public BigDecimal findAvailableQuantity(@NotNull final UUID siteUuid, @NotNull final UUID itemUuid) {
        final var item = itemService.findByUuid(itemUuid);
        final var site = siteService.findByUuid(siteUuid);
        var data = stockService.getRepository().sumQuantity(site.getId(), item.getId());
        if (Utils.isEmpty(data)) {
            data = BigDecimal.ZERO;
        }
        return data;
    }


    /**
     * @param response
     * @param filterForm
     * @param pageable
     * @return
     */
    public void xlsGenerator(final ServletOutputStream response,
                             @NotNull @Valid final TransferFilterForm filterForm,
                             final Pageable pageable) throws IOException {
        var projectionStream = this.filter(filterForm, pageable);
        final List<Transfer> transfers = projectionStream.getTransferListItemProjections().
            stream().map(TransferMapper::toMap).collect(Collectors.toList());
        var excel = new TransferListExcel();
        excel.generator(response, transfers);
    }
}
