package com.minetec.backend.controller;

import com.minetec.backend.dto.form.ItemAttributeCreateUpdateForm;
import com.minetec.backend.service.ItemAttributeService;
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

@RestController
@RequestMapping("/api/item-attribute")
@Secured(value = {"ROLE_ADMIN", "ROLE_WAREHOUSE_MANAGER", "ROLE_PROCUREMENT_MANAGER"})
public class ItemAttributeController extends BaseController<ItemAttributeService, ItemAttributeCreateUpdateForm> {

    @Override
    public ResponseEntity list(@PageableDefault(sort = "name", direction = Sort.Direction.ASC)
                                   final Pageable pageable) {
        return Response.ok(respFactory.success("18b7eh06d020", "item-attribute-listed",
            service.list(pageable)));
    }

    @Override
    public ResponseEntity create(@RequestBody @Valid final ItemAttributeCreateUpdateForm form) {
        return Response.ok(respFactory.success("28b7el06d028", "item-attribute-created",
            service.create(form)));
    }

    @Override
    public ResponseEntity update(@PathVariable final UUID uuid,
                                 @RequestBody @Valid final ItemAttributeCreateUpdateForm form) {
        return Response.ok(respFactory.success("38b7es06d022", "item-attribute-updated",
            service.update(uuid, form)));
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("48b7ex06d023", "item-attribute-fetched",
            service.find(uuid)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.delete(uuid);
        return Response.ok(respFactory.success("58b7ez06d026", "item-attribute-deleted"));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter,
                                 @PageableDefault(sort = "name", direction = Sort.Direction.ASC)
                                 final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("75b3ju07f063", "item-attribute-filtered", list));
    }

}
