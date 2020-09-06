package com.minetec.backend.service;

import com.minetec.backend.dto.filter.ItemOrderFilterForm;
import com.minetec.backend.dto.form.MessageCreateUpdateForm;
import com.minetec.backend.dto.form.OrderItemCreateUpdateForm;
import com.minetec.backend.dto.info.ItemOrderInfo;
import com.minetec.backend.dto.info.MessageInfo;
import com.minetec.backend.dto.info.OrderItemInfo;
import com.minetec.backend.dto.mapper.ItemOrderMapper;
import com.minetec.backend.dto.mapper.OrderItemMapper;
import com.minetec.backend.entity.Item;
import com.minetec.backend.entity.Order;
import com.minetec.backend.entity.OrderItem;
import com.minetec.backend.repository.OrderItemRepository;
import com.minetec.backend.repository.projection.OrderItemListItemProjection;
import com.minetec.backend.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.minetec.backend.entity.AbstractEntity.CURRENCY_SCALE;
import static com.minetec.backend.util.Utils.toBigDecimal;
import static org.springframework.data.jpa.domain.Specification.where;

/**
 * @author Sinan
 */
@Service
@RequiredArgsConstructor
public class OrderItemService extends EntityService<OrderItem, OrderItemRepository> {

    private final MessageService messageService;

    public Page<OrderItemListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * @param order
     * @param orderItemCreateUpdateForm
     * @return
     */
    public OrderItemInfo create(final Order order, final Item item,
                                final OrderItemCreateUpdateForm orderItemCreateUpdateForm) {
        var orderItem = new OrderItem();
        this.initEntity(orderItem, orderItemCreateUpdateForm);
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setApproveQuantity(toBigDecimal(orderItemCreateUpdateForm.getQuantity()));
        this.persist(orderItem);
        return OrderItemMapper.toInfo(orderItem);
    }

    /**
     * @param orderItemUuid
     * @param orderItemCreateUpdateForm
     * @return
     */
    public OrderItemInfo update(final UUID orderItemUuid, final OrderItemCreateUpdateForm orderItemCreateUpdateForm) {
        var orderItem = this.findByUuid(orderItemUuid);
        this.initEntity(orderItem, orderItemCreateUpdateForm);
        this.persist(orderItem);
        return OrderItemMapper.toInfo(orderItem);
    }

    /**
     * @param entity
     * @param orderItemCreateUpdateForm
     */
    private void initEntity(final OrderItem entity, final OrderItemCreateUpdateForm orderItemCreateUpdateForm) {
        entity.setQuantity(toBigDecimal(orderItemCreateUpdateForm.getQuantity()));
        entity.setBarcode(orderItemCreateUpdateForm.getBarcode());
        entity.setDescription(orderItemCreateUpdateForm.getDescription());
        entity.setDiscountPercent(toBigDecimal(orderItemCreateUpdateForm.getDiscountPercent()));
        entity.setTaxPercent(toBigDecimal(orderItemCreateUpdateForm.getTaxPercent()));
        entity.setTaxTotal(toBigDecimal(orderItemCreateUpdateForm.getTaxTotal()));
        entity.setTotal(toBigDecimal(orderItemCreateUpdateForm.getTotal()));
        entity.setTotalQuantity(toBigDecimal(orderItemCreateUpdateForm.getTotalQuantity()));
        entity.setUnitPrice(toBigDecimal(orderItemCreateUpdateForm.getUnitPrice()));
        entity.setUnit(orderItemCreateUpdateForm.getUnit());
    }

    /**
     * @param uuid
     * @return
     */
    public OrderItemInfo find(final UUID uuid) {
        return OrderItemMapper.toInfo(this.findByUuid(uuid));
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
    public Page<OrderItemListItemProjection> findBy(final String value, final Pageable pageable) {
        return this.getRepository().findAll(this.getFilter(value), pageable);
    }


    /**
     * @param filter
     * @return
     */
    public Specification<OrderItemListItemProjection> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("description", filter)
                .or(contains("barcode", filter)))
            .and(equals("isActive", true))
            .toPredicate(root, query, cb);
    }

    /**
     * @param orderItem
     * @param form
     * @return
     */
    public MessageInfo createOrderItemMessage(final OrderItem orderItem,
                                              final MessageCreateUpdateForm form) {
        return messageService.create(orderItem, form);
    }

    /**
     * @param orderItem
     * @param approveQuantity
     * @return
     */
    @Deprecated
    public boolean createApproveQuantity(final OrderItem orderItem,
                                         final BigDecimal approveQuantity) {
        var newOrderItem = this.findByUuid(orderItem.getUuid());
        if (!Utils.equals(newOrderItem.getQuantity(), approveQuantity)) {
            newOrderItem.setApprove(false);
        } else {
            newOrderItem.setApprove(true);
        }
        newOrderItem.setApproveQuantity(approveQuantity);
        this.persist(newOrderItem);
        return true;
    }

    /**
     * @param pageable
     * @return
     */
    public Page<MessageInfo> findByOrderItemId(@NotNull final UUID orderItemUuid,
                                               final Pageable pageable) {
        var orderItem = this.findByUuid(orderItemUuid);
        return messageService.findByOrderItemId(orderItem.getId(), pageable);
    }

    /**
     * @param orderItemUuid
     * @return
     */
    public List<OrderItem> findByOrderItemUuid(@NotNull final UUID orderItemUuid) {
        return this.getRepository().findByOrderItemUuid(orderItemUuid);
    }


    /**
     * @param form
     * @return
     */
    public Page<ItemOrderInfo> findAllItemOrder(@NotNull @Valid final ItemOrderFilterForm form) {
        Pageable pageable = PageRequest.of(form.getPage(), form.getSize(),
            Sort.by(Sort.Order.asc("id")));

        Page<OrderItem> orderItems = this.getRepository().filterBy(form.getItemUuid(),
            Utils.toLocalDate(form.getStartDate()), Utils.toLocalDate(form.getEndDate()), pageable);

        final List<ItemOrderInfo> items =
            orderItems.stream().map(ItemOrderMapper::toMap).collect(Collectors.toList());

        return new PageImpl<>(items, pageable, orderItems.getTotalElements());
    }

    /**
     * item id I needed
     *
     * @param siteId
     * @param itemId
     * @return
     */
    public BigDecimal findMaxTotalByItemId(@NotNull final Long siteId, @NotNull final Long itemId) {

        Map<Long, LocalDateTime> vehicles = new LinkedHashMap<>();
        final var ids = new ArrayList<Long>();

        final var orderItems = this.getRepository().findByOrderItemId(siteId, itemId);
        if (!Utils.isEmpty(orderItems)) {
            orderItems.forEach(orderItem -> {
                vehicles.put(orderItem.getOrder().getId(), orderItem.getOrder().getOrderCreationDate());
                ids.add(orderItem.getId());
            });

            Optional<Long> maxId = ids.stream().max(Comparator.naturalOrder());
            final var orderItem = this.getRepository().findById(maxId.get());
            return orderItem.get().getUnitPrice().setScale(CURRENCY_SCALE);
        }

        return BigDecimal.ZERO.setScale(CURRENCY_SCALE);
    }

}
