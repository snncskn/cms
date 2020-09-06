package com.minetec.backend.controller;

import com.minetec.backend.dto.filter.ItemFilterForm;
import com.minetec.backend.dto.filter.ItemOrderFilterForm;
import com.minetec.backend.dto.form.ItemAttributeListCreateForm;
import com.minetec.backend.dto.form.ItemCreateUpdateForm;
import com.minetec.backend.dto.form.ItemImageCreateForm;
import com.minetec.backend.service.ItemService;
import com.minetec.backend.util.Response;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Sinan
 */
@RestController
@RequestMapping("/api/item")
@Secured(value = {"ROLE_ADMIN", "ROLE_WAREHOUSE_MANAGER", "ROLE_PROCUREMENT_MANAGER"})
public class ItemController extends BaseController<ItemService, ItemCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final ItemCreateUpdateForm form) {
        return Response.ok(respFactory.success("18b7ed26d021", "item-created", service.create(form)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.delete(uuid);
        return Response.ok(respFactory.success("58b7ed36d020", "item-deleted"));
    }

    @Override
    public ResponseEntity filter(final String filter, final Pageable pageable) {
        return null;
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("28b7ed76d028", "item-found", service.find(uuid)));
    }

    @Override
    public ResponseEntity update(@PathVariable final UUID uuid, @RequestBody @Valid final ItemCreateUpdateForm form) {
        return Response.ok(respFactory.success("68b7ed26d025", "item-updated", service.update(uuid, form)));
    }

    @Override
    public ResponseEntity list(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("48b7ed56d029", "item-listed",
            service.findBy(new ItemFilterForm(), pageable)));
    }

    @PostMapping(value = {"/createItemList"})
    public ResponseEntity createItemList(@RequestBody @Valid final ItemAttributeListCreateForm form) {
        return Response.ok(respFactory.success("58b7ed76d027", "create-item-list", service.create(form)));
    }

    @GetMapping("/findItemList/{uuid}")
    public ResponseEntity findItemList(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("38b7ed06d023", "find-item-list", service.findItemDetail(uuid)));
    }

    @PostMapping("/createImages")
    public ResponseEntity createImages(@RequestBody @Valid final ItemImageCreateForm form) {
        return Response.ok(respFactory.success("10b5ef06l044", "item-create-images",
            service.createImages(form)));
    }

    @PostMapping(value = {"/find-all-item-order"})
    public ResponseEntity findAllItemOrder(@RequestBody @Valid final ItemOrderFilterForm form) {
        return Response.ok(respFactory.success("58b7ed76d027", "find-all-item-order", service.findAllItemOrder(form)));
    }

    @PostMapping(value = "/xls")
    public void xls(final HttpServletResponse response,
                    @RequestBody @Valid final ItemFilterForm filterForm) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RandomUtils.nextLong());
        service.xlsGenerator(response.getOutputStream(), filterForm);
    }

    @PostMapping("/filter")
    public ResponseEntity filter(@RequestBody final ItemFilterForm form) {
        return Response.ok(respFactory.success("65b7ec06d027", "item-filtered", service.filter(form)));
    }

}
