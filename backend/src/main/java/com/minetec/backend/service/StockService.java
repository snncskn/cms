package com.minetec.backend.service;

import com.minetec.backend.dto.filter.workshop.StockFilterForm;
import com.minetec.backend.dto.info.warehouse.StockHistoryInfo;
import com.minetec.backend.dto.mapper.StockHistoryMapper;
import com.minetec.backend.entity.OrderItem;
import com.minetec.backend.entity.Stock;
import com.minetec.backend.entity.StockHistory;
import com.minetec.backend.entity.warehouse.TransferItem;
import com.minetec.backend.entity.workshop.JobCardItem;
import com.minetec.backend.error_handling.exception.StockException;
import com.minetec.backend.repository.StockRepository;
import com.minetec.backend.util.Utils;
import com.minetec.backend.util.excel_generator.StockListExcel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.servlet.ServletOutputStream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.minetec.backend.entity.AbstractEntity.CURRENCY_SCALE;

@Service
@RequiredArgsConstructor
public class StockService extends EntityService<Stock, StockRepository> {

    private final SiteService siteService;
    private final StockHistoryService stockHistoryService;

    public boolean createOrderItems(@NotNull final List<OrderItem> orderItems) {
        orderItems.forEach(orderItem ->
            this.createOrderItem(orderItem, orderItem.getApproveQuantity()));
        return true;
    }

    public void createOrderItem(@NotNull final OrderItem orderItem, @NotNull final BigDecimal quantity) {
        var stock = this.getRepository().findBySiteIdAndItemId(
            orderItem.getOrder().getSite().getId(), orderItem.getItem().getId());
        if (Utils.isEmpty(stock)) {
            stock = new Stock();
            stock.setItem(orderItem.getItem());
            stock.setSite(orderItem.getOrder().getSite());
        }
        stock.setQuantity(stock.getQuantity().add(quantity));
        this.persist(stock);
    }

    public boolean createTransfer(@NotNull final List<TransferItem> transferItems) {

        transferItems.forEach(transferItem -> {
            var stock = this.getRepository().findBySiteIdAndItemId(
                transferItem.getTransfer().getSourceSite().getId(), transferItem.getItem().getId());
            if (Utils.isEmpty(stock)) {
                stock = new Stock();
                stock.setItem(transferItem.getItem());
                stock.setSite(transferItem.getTransfer().getSourceSite());
            }
            if (!Utils.isEmpty(stock.getQuantity())
                && stock.getQuantity().compareTo(transferItem.getApproveQuantity()) < 0) {
                throw new StockException("1hd01bkb1240", "No material in stock !");
            }
            stock.setQuantity(stock.getQuantity().subtract(transferItem.getApproveQuantity()));
            this.persist(stock);
        });

        transferItems.forEach(transferItem -> {
            var stock = this.getRepository().findBySiteIdAndItemId(
                transferItem.getTransfer().getTargetSite().getId(), transferItem.getItem().getId());
            if (Utils.isEmpty(stock)) {
                stock = new Stock();
                stock.setItem(transferItem.getItem());
                stock.setSite(transferItem.getTransfer().getTargetSite());
            }
            stock.setQuantity(stock.getQuantity().add(transferItem.getApproveQuantity()));
            this.persist(stock);
        });
        return true;
    }

    /**
     * @param jobCardItem
     * @param deliverQuantity
     * @return
     */
    public boolean createJobCard(@NotNull final JobCardItem jobCardItem, @NotNull final BigDecimal deliverQuantity) {
        var stock = this.getRepository().findBySiteIdAndItemId(
            jobCardItem.getJobCard().getVehicle().getSite().getId(), jobCardItem.getItem().getId());
        if (Utils.isEmpty(stock)) {
            stock = new Stock();
            stock.setItem(jobCardItem.getItem());
            stock.setSite(jobCardItem.getJobCard().getVehicle().getSite());
        }
        if (!Utils.isEmpty(stock.getQuantity())
            && stock.getQuantity().compareTo(deliverQuantity) < 0) {
            throw new StockException("8hd61xkc1280", "No material in stock !");
        }
        stock.setQuantity(stock.getQuantity().subtract(deliverQuantity));

        this.persist(stock);
        return true;
    }

    /**
     * @param form
     * @return
     */
    public Page<StockHistoryInfo> findByStockHistories(@NotNull final StockFilterForm form, final Pageable pageable) {

        final var site = siteService.findByUuid(this.getContextDetail().getSiteUuid());
        String siteName = form.getSiteName();
        if (Utils.isEmpty(siteName)) {
            siteName = site.getDescription();
        }
        Page<StockHistory> stockHistories = this.stockHistoryService.getRepository().filterBy(
            Utils.toLocalDate(form.getStartDate()), Utils.toLocalDate(form.getEndDate()),
            form.getStockCode(), form.getItemDescription(), siteName, pageable);
        final List<StockHistoryInfo> items =
            stockHistories.stream().map(StockHistoryMapper::toInfo).collect(Collectors.toList());
        return new PageImpl<>(items, pageable, stockHistories.getTotalElements());
    }


    /**
     *
     * @param filterForm
     * @param pageable
     * @return
     */
    public Page<StockHistoryInfo> findByCriteria(final StockFilterForm filterForm, final Pageable pageable) {
        List<StockHistory> stockHistories = this.stockHistoryService.getRepository().findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!Utils.isEmpty(filterForm.getStartDate()) && !Utils.isEmpty(filterForm.getEndDate())) {
                predicates.add(cb.between(root.get("createdAt"),
                    Utils.toLocalDate(filterForm.getStartDate()), Utils.toLocalDate(filterForm.getEndDate())));
            }

            if (!Utils.isEmpty(filterForm.getStockCode())) {
                predicates.add(
                    cb.like(root.get("item.storePartNumber"), "%" + filterForm.getStockCode() + "%"));
            }

            if (!Utils.isEmpty(filterForm.getItemDescription())) {
                predicates.add(
                    cb.like(root.get("item.itemDescription"), "%" + filterForm.getItemDescription() + "%"));
            }

            if (!Utils.isEmpty(filterForm.getSiteName())) {
                predicates.add(
                    cb.like(root.get("sourceSite.description"), "%" + filterForm.getSiteName() + "%"));
            }

            if (!Utils.isEmpty(filterForm.getMoveNumber())) {
                predicates.add(cb.like(root.get("transferItem.transfer.transferNumber"),
                    "%" + filterForm.getMoveNumber() + "%"));
                predicates.add(cb.like(root.get("jobCardItem.jobCard.reportNumber"),
                    "%" + filterForm.getMoveNumber() + "%"));
                predicates.add(cb.like(root.get("orderItem.order.invoiceNumber"),
                    "%" + filterForm.getMoveNumber() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        });

        final List<StockHistoryInfo> infos =
            stockHistories.stream().map(StockHistoryMapper::toInfo).collect(Collectors.toList());
        return new PageImpl<>(infos, pageable, stockHistories.size());
    }


    /**
     * @param response
     * @param filterForm
     * @return
     */
    public void xlsGenerator(final ServletOutputStream response,
                             @NotNull @Valid final StockFilterForm filterForm) throws IOException {
        Pageable pageable = PageRequest.of(filterForm.getPage(), filterForm.getSize());
        var stockHistories = findByStockHistories(filterForm, pageable);
        var excel = new StockListExcel();
        excel.generator(response, stockHistories);
    }

    /**
     * @param siteId
     * @param itemId
     * @return
     */
    public BigDecimal findCurrentQuantity(@NotNull final Long siteId, @NotNull final Long itemId) {
        final var qty = this.getRepository().sumQuantity(siteId, itemId);
        if (!Utils.isEmpty(qty)) {
            return qty.setScale(CURRENCY_SCALE);
        }
        return BigDecimal.ZERO.setScale(CURRENCY_SCALE);
    }
}
