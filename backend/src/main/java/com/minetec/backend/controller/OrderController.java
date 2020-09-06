package com.minetec.backend.controller;

import com.minetec.backend.dto.filter.OrderFilterForm;
import com.minetec.backend.dto.form.ApproveRejectForm;
import com.minetec.backend.dto.form.DateFilterForm;
import com.minetec.backend.dto.form.MessageCreateUpdateForm;
import com.minetec.backend.dto.form.OrderApproveForm;
import com.minetec.backend.dto.form.OrderCreateUpdateForm;
import com.minetec.backend.dto.form.OrderInvoiceForm;
import com.minetec.backend.dto.form.OrderItemCreateUpdateForm;
import com.minetec.backend.service.OrderService;
import com.minetec.backend.util.Response;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Sinan
 */
@RestController
@RequestMapping("/api/order")
@Secured(value = {"ROLE_ADMIN", "ROLE_PROCUREMENT_MANAGER", "ROLE_SITE_BUYER", "ROLE_SITE_MANAGER"})
public class OrderController extends BaseController<OrderService, OrderCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final OrderCreateUpdateForm form) {
        return Response.ok(respFactory.success("12b7ed06d029", "order-created", service.create(form)));
    }

    @Override
    public ResponseEntity delete(@RequestParam @NotNull final UUID uuid) {
        service.softDelete(uuid);
        return Response.ok(respFactory.success("15b7ef06d025", "order-deleted"));
    }

    @Override
    public ResponseEntity fetch(@PathVariable @NotNull final UUID uuid) {
        return Response.ok(respFactory.success("19b7vd06d014", "order-fetched", service.find(uuid)));
    }

    @Override
    public ResponseEntity update(@RequestParam @NotNull final UUID uuid,
                                 @RequestBody @Valid final OrderCreateUpdateForm form) {
        return Response.ok(
            respFactory.success("27b7ed06d020", "order-updated", service.update(uuid, form)));
    }

    @PostMapping("/list-all-order")
    public ResponseEntity listAllOrder(@RequestBody final OrderFilterForm form) {
        Pageable pageable = PageRequest.of(form.getPage(), form.getSize(),
            Sort.by(Sort.Order.desc("createdAt")));
        return Response.ok(respFactory.success("65b7ec06d027", "order-listed",
            service.filter(form, pageable)));
    }

    @PostMapping("/approve-reject")
    public ResponseEntity approveReject(@RequestBody @Valid final ApproveRejectForm form) {
        return Response.ok(
            respFactory.success("68b1ed06d014", "approve-reject", service.approveReject(form)));
    }

    @PostMapping("/create-order-item")
    public ResponseEntity createOrderItem(@RequestBody @Valid final OrderItemCreateUpdateForm form) {
        return Response.ok(
            respFactory.success("23b7ef06d089", "order-item-created", service.createOrderItem(form)));
    }

    @DeleteMapping("delete-order-item")
    public ResponseEntity deleteOrderItem(@RequestParam @NotNull final UUID orderItemUuid) {
        final var result = service.softDeleteOrderItem(orderItemUuid);
        if (result) {
            return Response.ok(respFactory.success("43b6ed02d019", "order-item-deleted"));
        } else {
            return Response.ok(respFactory.error("13b7ed05d089", "order-item-deleted-error"));
        }
    }

    @PutMapping("update-order-item")
    public ResponseEntity updateOrderItem(@RequestParam @NotNull final UUID orderItemUuid,
                                          @RequestBody @Valid final OrderItemCreateUpdateForm form) {
        return Response.ok(respFactory.success("23b7ed02d039", "order-item-updated",
            service.updateOrderItem(orderItemUuid, form)));
    }

    @GetMapping("/find-order-item/{orderItemUuid}")
    public ResponseEntity findOrderItem(@PathVariable @NotNull final UUID orderItemUuid) {
        return Response.ok(respFactory.success("18b4ed06d054", "order-item-found",
            service.findOrderItem(orderItemUuid)));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter, final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("28b7ed07e084", "order-filtered", list));
    }

    @PostMapping("/create-order-item-message")
    public ResponseEntity createOrderItemMessage(@RequestBody @Valid final MessageCreateUpdateForm form) {
        return Response.ok(respFactory.success("23b7ed06d089", "create-order-item-message",
            service.createOrderItemMessage(form)));
    }

    @PostMapping("/create-approve-quantity")
    public ResponseEntity createApproveQuantity(@RequestBody @Valid final OrderApproveForm form) {
        var ret = service.createApproveQuantity(form);
        final var status = service.prepareOrderStatus(form);
        ret.setOrderStatus(status);
        return Response.ok(respFactory.success("23b7ed06d089", ret.getMessage(), ret));
    }

    @GetMapping("/list-order-item-messages/{orderItemUuid}")
    public ResponseEntity listOrderItemMessages(@PathVariable @NotNull final UUID orderItemUuid,
                                                @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                                                final Pageable pageable) {
        return Response.ok(respFactory.success("23b7ed06d089", "list-order-item-message",
            service.findByOrderItemId(orderItemUuid, pageable)));
    }

    @PutMapping("/update-invoice-info")
    public ResponseEntity updateInvoiceInfo(@RequestParam @NotNull final UUID orderUuid,
                                            @RequestBody @NotNull @Valid final OrderInvoiceForm form) {
        final var result = service.updateInvoiceInfo(orderUuid, form);
        if (result) {
            return Response.ok(respFactory.success("43b6ed02d019", "updated-invoice-info"));
        } else {
            return Response.ok(respFactory.error("13b7ed05d089", "error-invoice-info"));
        }
    }

    @Override
    public ResponseEntity list(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("65b7ec06d027", "order-listed", service.list(pageable)));
    }

    @PostMapping("/order-counts")
    public ResponseEntity orderCounts(@RequestBody @Valid final DateFilterForm form) {
        return Response.ok(respFactory.success("23b7ed06d089", "order-counts",
            service.orderCounts(form)));
    }

    @PostMapping(value = "/xls")
    public void xls(final HttpServletResponse response,
                    @RequestBody @Valid final OrderFilterForm filterForm) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RandomUtils.nextLong());
        service.xlsGenerator(response.getOutputStream(), filterForm);
    }

    @GetMapping("/find-order-item-detail/{orderItemUuid}")
    public ResponseEntity findOrderItemDetail(@PathVariable @NotNull final UUID orderItemUuid,
                                              @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                                              final Pageable pageable) {
        return Response.ok(respFactory.success("18b4ed06d054", "order-item-detail-found",
                service.findOrderItemDetailByOrderItem(orderItemUuid, pageable)));
    }

}
