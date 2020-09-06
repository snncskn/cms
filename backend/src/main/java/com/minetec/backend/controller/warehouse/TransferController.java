package com.minetec.backend.controller.warehouse;

import com.minetec.backend.controller.BaseController;
import com.minetec.backend.dto.filter.warehouse.TransferFilterForm;
import com.minetec.backend.dto.form.ApproveRejectForm;
import com.minetec.backend.dto.form.DateFilterForm;
import com.minetec.backend.dto.form.MessageCreateUpdateForm;
import com.minetec.backend.dto.form.warehouse.TransferApproveForm;
import com.minetec.backend.dto.form.warehouse.TransferCreateUpdateForm;
import com.minetec.backend.dto.form.warehouse.TransferDeliverForm;
import com.minetec.backend.dto.form.warehouse.TransferItemCreateUpdateForm;
import com.minetec.backend.service.warehouse.TransferService;
import com.minetec.backend.util.Response;
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
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Sinan
 */

@RestController
@RequestMapping("/api/transfer")
@Secured(value = {"ROLE_ADMIN", "ROLE_PROCUREMENT_MANAGER", "ROLE_SITE_BUYER", "ROLE_SITE_MANAGER"})
public class TransferController extends BaseController<TransferService, TransferCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final TransferCreateUpdateForm form) {
        return Response.ok(respFactory.success("12b7ed06d029", "transfer-created", service.create(form)));
    }

    @Override
    public ResponseEntity delete(@RequestParam @NotNull final UUID uuid) {
        service.softDelete(uuid);
        return Response.ok(respFactory.success("15b7ef06d025", "transfer-deleted"));
    }

    @Override
    public ResponseEntity fetch(@PathVariable @NotNull final UUID uuid) {
        return Response.ok(respFactory.success("19b7vd06d014", "transfer-fetched", service.find(uuid)));
    }

    @Override
    public ResponseEntity update(@RequestParam @NotNull final UUID uuid,
                                 @RequestBody @Valid final TransferCreateUpdateForm form) {
        return Response.ok(
            respFactory.success("27b7ed06d020", "transfer-updated", service.update(uuid, form)));
    }

    @PostMapping("/approve-reject")
    public ResponseEntity approveReject(@RequestBody @Valid final ApproveRejectForm form) {
        return Response.ok(
            respFactory.success("68b1ed06d014", "approve-reject", service.approveReject(form)));
    }

    @PostMapping("/create-transfer-item")
    public ResponseEntity createTransferItem(@RequestBody @Valid final TransferItemCreateUpdateForm form) {
        return Response.ok(
            respFactory.success("23b7ef06d089", "transfer-item-created", service.createTransferItem(form)));
    }

    @DeleteMapping("delete-transfer-item")
        public ResponseEntity deleteTransferItem(@RequestParam @NotNull final UUID transferItemUuid) {
        service.softDeleteTransferItem(transferItemUuid);
        return Response.ok(respFactory.success("25b7ef03d028", "transfer-item-deleted"));
    }

    @PutMapping("update-transfer-item")
    public ResponseEntity updateTransferItem(@RequestParam @NotNull final UUID transferItemUuid,
                                             @RequestBody @Valid final TransferItemCreateUpdateForm form) {
        return Response.ok(respFactory.success("23b7ed02d039", "transfer-item-updated",
            service.updateTransferItem(transferItemUuid, form)));
    }

    @GetMapping("/find-transfer-item/{transferItemUuid}")
    public ResponseEntity findTransferItem(@PathVariable @NotNull final UUID transferItemUuid) {
        return Response.ok(respFactory.success("18b4ed06d054", "transfer-item-found",
            service.findTransferItem(transferItemUuid)));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter, final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("28b7ed07e084", "transfer-filtered", list));
    }

    @PostMapping("/create-transfer-item-message")
    public ResponseEntity createTransferItemMessage(@RequestBody @Valid final MessageCreateUpdateForm form) {
        return Response.ok(respFactory.success("23b7ed06d089", "create-transfer-item-message",
            service.createTransferItemMessage(form)));
    }

    @PostMapping("/create-approve-quantity")
    public ResponseEntity createApproveQuantity(@RequestBody @Valid final TransferApproveForm form) {
        return Response.ok(respFactory.success("23b7ed06d089", "create-approve-quantity",
            service.createApproveQuantity(form)));
    }

    @GetMapping("/list-transfer-item-messages/{transferItemUuid}")
    public ResponseEntity listTransferItemMessages(@PathVariable @NotNull final UUID transferItemUuid,
                                                   @PageableDefault(sort = "id", direction = Sort.Direction.DESC)
                                                   final Pageable pageable) {
        return Response.ok(respFactory.success("23b7ed06d089", "list-transfer-item-message",
            service.findByTransferItemId(transferItemUuid, pageable)));
    }

    @PutMapping("/update-deliver-info")
    public ResponseEntity updateDeliverInfo(@RequestParam @NotNull final UUID transferUuid,
                                            @RequestBody @NotNull @Valid final TransferDeliverForm form) {
        final var result = service.updateDeliverInfo(transferUuid, form);
        if (result) {
            return Response.ok(respFactory.success("43b6ed02d019", "updated-invoice-info"));
        } else {
            return Response.ok(respFactory.error("13b7ed05d089", "error-invoice-info"));
        }
    }

    @Override
    public ResponseEntity list(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("65b7ec06d027", "transfer-listed", service.list(pageable)));
    }

    @PostMapping("/transfer-counts")
    public ResponseEntity transferCounts(@RequestBody @Valid final DateFilterForm form) {
        return Response.ok(respFactory.success("23b7ed06d089", "transfer-counts",
            service.transferCounts(form)));
    }

    @GetMapping("/find-available-quantity/{siteUuid}/{itemUuid}")
    public ResponseEntity findAvailableQuantity(@PathVariable @NotNull final UUID siteUuid,
                                                @PathVariable @NotNull final UUID itemUuid) {
        return Response.ok(respFactory.success("52b4eu06d022", "find-available-quantity",
            service.findAvailableQuantity(siteUuid, itemUuid)));
    }

    @PostMapping("/list-all-transfer")
    public ResponseEntity listAllTransfer(@RequestBody final TransferFilterForm form, final Pageable pageable) {
        var ret = respFactory.success("65b7ec06d027", "transfer-listed", service.filter(form, pageable));
        return Response.ok(ret);
    }

    @PostMapping(value = "/xls", produces = "application/vnd.ms-excel")
    public void xls(final HttpServletResponse response,
                    @RequestBody @Valid final TransferFilterForm filterForm,
                    final Pageable pageable) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        var sb = new StringBuilder(this.getClass().getSimpleName()).append("_").append(LocalDateTime.now());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + sb.toString());
        service.xlsGenerator(response.getOutputStream(), filterForm, pageable);
    }

}
