package com.minetec.backend.service;

import com.minetec.backend.dto.enums.OrderStatus;
import com.minetec.backend.dto.filter.OrderFilterForm;
import com.minetec.backend.dto.form.ApproveRejectForm;
import com.minetec.backend.dto.form.DateFilterForm;
import com.minetec.backend.dto.form.MessageCreateUpdateForm;
import com.minetec.backend.dto.form.OrderApproveForm;
import com.minetec.backend.dto.form.OrderCreateUpdateForm;
import com.minetec.backend.dto.form.OrderInvoiceForm;
import com.minetec.backend.dto.form.OrderItemCreateUpdateForm;
import com.minetec.backend.dto.info.BasicUserInfo;
import com.minetec.backend.dto.info.ImageInfo;
import com.minetec.backend.dto.info.MessageInfo;
import com.minetec.backend.dto.info.OrderInfo;
import com.minetec.backend.dto.info.OrderItemDetailInfo;
import com.minetec.backend.dto.info.OrderItemInfo;
import com.minetec.backend.dto.info.OrderListResponseInfo;
import com.minetec.backend.dto.info.OrderResponseInfo;
import com.minetec.backend.dto.mapper.ImageMapper;
import com.minetec.backend.dto.mapper.OrderItemMapper;
import com.minetec.backend.dto.mapper.OrderMapper;
import com.minetec.backend.entity.Order;
import com.minetec.backend.entity.OrderItem;
import com.minetec.backend.repository.OrderRepository;
import com.minetec.backend.repository.projection.OrderListItemProjection;
import com.minetec.backend.util.Utils;
import com.minetec.backend.util.excel_generator.OrderListExcel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import static com.minetec.backend.dto.enums.OrderStatus.INVOICE;
import static com.minetec.backend.dto.enums.OrderStatus.ORDER;
import static com.minetec.backend.dto.enums.OrderStatus.PARTIAL;
import static com.minetec.backend.entity.AbstractEntity.CURRENCY_SCALE;
import static org.springframework.data.jpa.domain.Specification.where;

/**
 * @author Sinan
 */
@Service
@RequiredArgsConstructor
public class OrderService extends EntityService<Order, OrderRepository> {

    private static final int NUMBER_LENGTH = 10;

    private final ItemService itemService;
    private final ImageService imageService;
    private final SiteService siteService;
    private final SupplierService supplierService;
    private final OrderItemService orderItemService;
    private final OrderHistoryService orderHistoryService;
    private final JdbcTemplate jdbcTemplate;
    private final StockService stockService;
    private final StockHistoryService stockHistoryService;
    private final OrderItemDetailService orderItemDetailService;

    /**
     * @param pageable
     * @return
     */
    public Page<OrderListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * @param form
     * @return
     */
    public OrderListResponseInfo filter(@NotNull final OrderFilterForm form, final Pageable pageable) {

        var responseInfo = new OrderListResponseInfo();

        LocalDateTime requestStartDate = null, requestEndDate = null,
            orderCreationStartDate = null, orderCreationEndDate = null,
            invoiceStartDate = null, invoiceEndDate = null;

        Page<OrderListItemProjection> list = null;


        final var site = siteService.findByUuid(this.getContextDetail().getSiteUuid());

        switch (OrderStatus.valueOf(form.getStatus())) {
            case PURCHASE_REQUEST:
                requestStartDate = Utils.toLocalDate(form.getStartDate());
                requestEndDate = Utils.toLocalDate(form.getEndDate());
                list = getRepository().filterByRequestDate(requestStartDate, requestEndDate, site.getId(),
                    form.getRequestNumber(), form.getSupplierName(), pageable);
                responseInfo.setRequestCounts(list.getTotalElements());
                break;
            case ORDER:
                orderCreationStartDate = Utils.toLocalDate(form.getStartDate());
                orderCreationEndDate = Utils.toLocalDate(form.getEndDate());
                list = getRepository().filterByOrderCreationDate(orderCreationStartDate, orderCreationEndDate,
                    site.getId(), form.getOrderNumber(), form.getSupplierName(), pageable);
                responseInfo.setOrderCounts(list.getTotalElements());
                break;
            case INVOICE:
                invoiceStartDate = Utils.toLocalDate(form.getStartDate());
                invoiceEndDate = Utils.toLocalDate(form.getEndDate());
                list = getRepository().filterByInvoiceDate(invoiceStartDate, invoiceEndDate,
                    site.getId(), form.getInvoiceNumber(), form.getSupplierName(), pageable);
                responseInfo.setInvoiceCounts(list.getTotalElements());
                break;
            case REJECTED:
                list = getRepository().filterByReject(site.getId(), pageable);
                responseInfo.setRejectCounts(list.getTotalElements());
                break;
            default:
                break;
        }

        responseInfo.setOrderListItemProjections(list);

        return responseInfo;
    }

    /**
     * @param orderCreateUpdateForm
     * @return
     */
    public OrderInfo create(final OrderCreateUpdateForm orderCreateUpdateForm) {
        var entity = new Order();
        var newSeq =
            String.valueOf(jdbcTemplate.queryForObject("SELECT nextval('REQUEST_NUMBER_SEQ')", Long.class));
        entity.setRequestNumber("PR ".concat(StringUtils.leftPad(newSeq, NUMBER_LENGTH, "0")));
        entity.setRequestDate(Utils.toLocalDate(orderCreateUpdateForm.getRequestDate()));
        this.initEntity(orderCreateUpdateForm, entity);
        return OrderMapper.toInfo(this.persist(entity));
    }

    /**
     * @param uuid
     * @param orderCreateUpdateForm
     * @return
     */
    public OrderInfo update(final UUID uuid, final OrderCreateUpdateForm orderCreateUpdateForm) {
        var entity = this.findByUuid(uuid);
        this.initEntity(orderCreateUpdateForm, entity);
        return OrderMapper.toInfo(this.persist(entity));
    }

    /**
     * @param orderCreateUpdateForm
     * @param entity
     */
    private void initEntity(final OrderCreateUpdateForm orderCreateUpdateForm, final Order entity) {
        entity.setSite(siteService.findByUuid(orderCreateUpdateForm.getSiteUuid()));
        entity.setSupplier(supplierService.findByUuid(orderCreateUpdateForm.getSupplierUuid()));
        entity.setStatus(OrderStatus.valueOf(orderCreateUpdateForm.getStatus()));
        entity.setOrderCreationDate(Utils.toLocalDate(orderCreateUpdateForm.getOrderCreationDate()));
        entity.setInvoiceDate(Utils.toLocalDate(orderCreateUpdateForm.getInvoiceDate()));
        entity.setInvoiceNumber(orderCreateUpdateForm.getInvoiceNumber());
        entity.setTotalQuantity(Utils.toBigDecimal(orderCreateUpdateForm.getTotalQuantity()));
        entity.setReferenceNumber(orderCreateUpdateForm.getReferenceNumber());
        entity.setGrandTotal(Utils.toBigDecimal(orderCreateUpdateForm.getGrandTotal()));
        entity.setDiscountTotal(Utils.toBigDecimal(orderCreateUpdateForm.getDiscountTotal()));
        entity.setTaxTotal(Utils.toBigDecimal(orderCreateUpdateForm.getTaxTotal()));
        entity.setAddressDetail(orderCreateUpdateForm.getAddressDetail());
    }

    /**
     * @param uuid
     * @return
     */
    public OrderInfo find(final UUID uuid) {
        var order = this.findByUuid(uuid);
        var orderInfo = OrderMapper.toInfo(order);
        var orderItems = orderItemService.getRepository().findByOrderId(order.getId());
        var tmpItems = new ArrayList<OrderItemInfo>();
        orderItems.forEach(item -> {
            var orderItem = OrderItemMapper.toInfo(item);
            if (ORDER.equals(order.getStatus()) || PARTIAL.equals(order.getStatus())) {

                BigDecimal usedOrderItemQty = stockHistoryService.getRepository().sumInputQuantity(
                    order.getSite().getId(), orderItem.getId());

                if (Utils.isEmpty(usedOrderItemQty)) {
                    usedOrderItemQty = BigDecimal.ZERO;
                }

                orderItem.setApproveQuantity(
                    orderItem.getQuantity().setScale(CURRENCY_SCALE).subtract(usedOrderItemQty.setScale(CURRENCY_SCALE))
                );

            }
            tmpItems.add(orderItem);
        });
        orderInfo.setOrderItemInfos(tmpItems);
        orderInfo.setBasicUserInfo(basicInfoMap(order));

        List<ImageInfo> imageInfos = ImageMapper.toInfos(order.getSupplier().getImages());
        imageInfos.forEach(imageInfo -> {
            imageInfo.setDownloadUrl(imageService.imageUrl(imageInfo.getUuid()));
        });

        orderInfo.getSupplierInfo().setImageInfos(imageInfos);
        return orderInfo;
    }

    /**
     * @param order
     * @return
     */
    private BasicUserInfo basicInfoMap(final Order order) {
        var basicInfo = new BasicUserInfo();
        basicInfo.setCreateDate(Utils.toString(order.getCreatedAt()));
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
     * @param orderItemCreateUpdateForm
     * @return
     */
    public OrderItemInfo createOrderItem(final OrderItemCreateUpdateForm orderItemCreateUpdateForm) {
        final var order = this.findByUuid(orderItemCreateUpdateForm.getOrderUuid());
        final var item = itemService.findByUuid(orderItemCreateUpdateForm.getItemUuid());
        return orderItemService.create(order, item, orderItemCreateUpdateForm);
    }

    /**
     * @param orderItemUuid
     * @return
     */
    public OrderItemInfo findOrderItem(@NotNull final UUID orderItemUuid) {
        var orderItem = orderItemService.findByUuid(orderItemUuid);
        return OrderItemMapper.toInfo(orderItem);
    }

    /**
     * @param orderItemUuid
     */
    public boolean softDeleteOrderItem(@NotNull final UUID orderItemUuid) {
        var orderItem = orderItemService.findByUuid(orderItemUuid);
        BigDecimal usedOrderItem = stockHistoryService.getRepository().sumInputQuantity(
            orderItem.getOrder().getSite().getId(), orderItem.getId());
        if (Utils.isEmpty(usedOrderItem)) {
            orderItemService.softDelete(orderItemUuid);
            return true;
        }
        return false;
    }

    /**
     * approveReject
     *
     * @return
     */
    public boolean approveReject(@NotNull final ApproveRejectForm approveRejectForm) {

        var order = this.findByUuid(approveRejectForm.getOrderUuid());
        order.setStatus(OrderStatus.valueOf(approveRejectForm.getStatus()));
        order.setRejectionReason(approveRejectForm.getRejectionReason());

        switch (OrderStatus.valueOf(approveRejectForm.getStatus())) {
            case ORDER:
                order.setOrderCreationDate(LocalDateTime.now());
                order.setOrderNumber(order.getRequestNumber().replaceAll("PR", "PO"));
                break;
            default:
                break;
        }

        var newOrder = this.persist(order);
        orderHistoryService.create(newOrder);

        if (INVOICE.equals(newOrder.getStatus())) {
            /**
             * stock entered for items
             */
            final var orderItems = newOrder.getOrderItems();
            if (!Utils.isEmpty(orderItems)) {
                order.getOrderItems().forEach(orderItem -> {
                    var approveForm = new OrderApproveForm();
                    approveForm.setOrderItem(orderItem);
                    approveForm.setApproveQuantity(orderItem.getQuantity());
                    approveForm.setOrderItemUuid(orderItem.getUuid());
                    this.createApproveQuantity(approveForm);
                });
            }
        }


        return true;
    }

    /**
     * @param orderItemUuid
     * @param orderItemCreateUpdateForm
     * @return
     */
    public Object updateOrderItem(final UUID orderItemUuid, final OrderItemCreateUpdateForm orderItemCreateUpdateForm) {
        return orderItemService.update(orderItemUuid, orderItemCreateUpdateForm);
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<OrderListItemProjection> findBy(final String value, final Pageable pageable) {
        return this.getRepository().findAll(this.getFilter(value), pageable);
    }

    /**
     * @param filter
     * @return
     */
    private Specification<OrderListItemProjection> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("requestNumber", filter)
                .or(contains("orderNumber", filter))
                .or(contains("invoiceNumber", filter))
                .or(contains("referenceNumber", filter)))
            .and(equals("isActive", true))
            .toPredicate(root, query, cb);
    }

    /**
     * @param form
     * @return
     */
    public MessageInfo createOrderItemMessage(final MessageCreateUpdateForm form) {
        var orderItem = orderItemService.findByUuid(form.getOrderItemUuid());
        return orderItemService.createOrderItemMessage(orderItem, form);
    }

    /**
     * @param form
     * @return
     */
    public OrderResponseInfo createApproveQuantity(final OrderApproveForm form) {

        var response = new OrderResponseInfo();
        var orderItem = form.getOrderItem();

        if (Utils.isEmpty(orderItem)) {
            orderItem = orderItemService.findByUuid(form.getOrderItemUuid());
        }

        final var order = this.findOrderByOrderItemId(orderItem.getUuid());

        if (PARTIAL.equals(order.getStatus()) || ORDER.equals(order.getStatus()) || INVOICE.equals(order.getStatus())) {
            /**
             * stock validation, when this item used.
             */
            BigDecimal usedOrderItemQty = stockHistoryService.getRepository().sumInputQuantity(
                orderItem.getOrder().getSite().getId(), orderItem.getId());

            if (Utils.isEmpty(usedOrderItemQty)) {
                usedOrderItemQty = BigDecimal.ZERO;
            }

            if (orderItem.getQuantity().compareTo(form.getApproveQuantity().add(usedOrderItemQty)) == -1) {

                response.setResponseStatus("WARNING");
                response.setMessage("create-approve-quantity-bigger-than-stock");

            } else {

                BigDecimal quantity = form.getApproveQuantity();

                response.setResponseStatus("SUCCESS");
                response.setMessage("create-approve-quantity-stock-entered");

                stockService.createOrderItem(orderItem, quantity);
                stockHistoryService.createOrderItem(orderItem, quantity);
                orderItemDetailService.create(orderItem, quantity);

            }

        }

        // final var orderStatus = prepareOrderStatus(order);
        // response.setOrderStatus(orderStatus);

        return response;
    }

    /**
     *
     * @param form
     * @return
     */
    public OrderStatus prepareOrderStatus(final OrderApproveForm form) {

        final var order = this.findOrderByOrderItemId(form.getOrderItemUuid());

        OrderStatus orderStatus = ORDER;

        for (OrderItem orderItem : order.getOrderItems()) {

            BigDecimal stockOrderItemQty = stockHistoryService.getRepository().sumInputQuantity(
                orderItem.getOrder().getSite().getId(), orderItem.getId());

            if (Utils.isEmpty(stockOrderItemQty)) {
                stockOrderItemQty = BigDecimal.ZERO;
            }

            if (!Utils.equals(orderItem.getQuantity(), stockOrderItemQty)) {
                orderStatus = PARTIAL;
                orderItem.setApprove(false);
                orderItemService.persist(orderItem);
                break;
            } else {
                orderItem.setApprove(true);
                orderItemService.persist(orderItem);
            }
        }

        order.setStatus(orderStatus);
        this.persist(order);
        return orderStatus;
    }

    /**
     * @param pageable
     * @return
     */
    public Page<MessageInfo> findByOrderItemId(final UUID orderItemUuid,
                                               final Pageable pageable) {
        return orderItemService.findByOrderItemId(orderItemUuid, pageable);
    }

    /**
     * @param orderUuid
     * @param form
     * @return
     */
    public boolean updateInvoiceInfo(final UUID orderUuid, final OrderInvoiceForm form) {
        var order = this.findByUuid(orderUuid);
        order.setInvoiceDate(Utils.toLocalDate(form.getInvoiceDate()));
        order.setInvoiceNumber(form.getInvoiceNumber());
        order.setStatus(INVOICE);
        this.persist(order);
        return true;
    }

    /**
     * @param orderItemUuid
     * @return
     */
    public Order findOrderByOrderItemId(final UUID orderItemUuid) {
        return orderItemService.getRepository().findByOrder(orderItemUuid);
    }

    /**
     * @param form
     * @return
     */
    public Long orderCounts(@NotNull final DateFilterForm form) {
        LocalDateTime startDate = Utils.toLocalDate(form.getStartDate());
        LocalDateTime endDate = Utils.toLocalDate(form.getEndDate());
        Long count = 0L;
        switch (OrderStatus.valueOf(form.getStatus())) {
            case PURCHASE_REQUEST:
                count = getRepository().filterByRequestCount(startDate, endDate);
                break;
            case ORDER:
                count = getRepository().filterByOrderCreationCount(startDate, endDate);
                break;
            case INVOICE:
                count = getRepository().filterByInvoiceCount(startDate, endDate);
                break;
            case REJECTED:
                count = getRepository().filterByRejectCount();
                break;
            default:
                break;
        }
        return count;
    }

    /**
     * @param response
     * @param filterForm
     */
    public void xlsGenerator(final ServletOutputStream response,
                             @NotNull @Valid final OrderFilterForm filterForm) throws IOException {
        Pageable pageable = PageRequest.of(filterForm.getPage(), filterForm.getSize());
        var orderListItemProjections = this.filter(filterForm, pageable);
        var orderListExcel = new OrderListExcel();
        orderListExcel.generator(response, orderListItemProjections.getOrderListItemProjections());
    }


    /**
     * @param orderItemUuid
     * @param pageable
     * @return
     */
    public Page<OrderItemDetailInfo> findOrderItemDetailByOrderItem(@NotNull final UUID orderItemUuid,
                                                                    final Pageable pageable) {
        final var orderItem = orderItemService.findByUuid(orderItemUuid);
        return orderItemDetailService.list(orderItem.getId(), pageable);
    }

}
