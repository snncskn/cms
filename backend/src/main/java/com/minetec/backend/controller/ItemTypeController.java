package com.minetec.backend.controller;

import com.minetec.backend.dto.form.ItemTypeCreateUpdateForm;
import com.minetec.backend.service.ItemTypeService;
import com.minetec.backend.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;


/**
 * @author Sinan
 */

@RestController
@RequestMapping("/api/item-type")
@Secured(value = {"ROLE_ADMIN", "ROLE_WAREHOUSE_MANAGER", "ROLE_PROCUREMENT_MANAGER"})
public class ItemTypeController extends BaseController<ItemTypeService, ItemTypeCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final ItemTypeCreateUpdateForm form) {
        return Response.ok(respFactory.success("7720095253d8", "item-type-created", service.create(form)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.softDelete(uuid);
        return Response.ok(respFactory.success("91099cf05bee", "item-type-deleted"));
    }

    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("3d895b285013", "item-type-fetched", service.find(uuid)));
    }

    @Override
    public ResponseEntity list(
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(
            respFactory.success("32221dff10a8", "item-type-listed", service.list(pageable)));
    }

    @Override
    public ResponseEntity update(@RequestParam final UUID uuid,
                                 @RequestBody @Valid final ItemTypeCreateUpdateForm form) {
        return Response.ok(respFactory.success("c73fb35d9016", "item-type-updated", service.create(form)));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter,
                                 @PageableDefault(sort = "name", direction = Sort.Direction.ASC)
                                 final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("28b7ed07e084", "item-type-filtered", list));
    }
}
