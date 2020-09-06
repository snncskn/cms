package com.minetec.backend.controller;

import com.minetec.backend.dto.form.ItemAttributeValueCreateUpdateForm;
import com.minetec.backend.service.ItemAttributeService;
import com.minetec.backend.service.ItemAttributeValueService;
import com.minetec.backend.util.Response;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/item-attribute-value")
@RequiredArgsConstructor
@Secured(value = {"ROLE_ADMIN", "ROLE_WAREHOUSE_MANAGER", "ROLE_PROCUREMENT_MANAGER"})
public class ItemAttributeValueController
    extends BaseController<ItemAttributeValueService, ItemAttributeValueCreateUpdateForm> {

    private final ItemAttributeService itemAttributeService;

    @Override
    public ResponseEntity list(@PageableDefault(sort = "name", direction = Sort.Direction.ASC)
                                   final Pageable pageable) {
        return Response.ok(
            respFactory.success("58b7ed06d024", "item-attribute-value-listed", service.list(pageable)));
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(
            respFactory.success("58b7ed06d024", "item-attribute-value-fetched", service.find(uuid)));
    }

    @Override
    public ResponseEntity create(@RequestBody @Valid final ItemAttributeValueCreateUpdateForm form) {
        final var itemAttribute = itemAttributeService.findByUuid(form.getVehicleAttributeUuid());
        final var itemAttributeValueInfo = service.create(itemAttribute, form);
        return Response.ok(
            respFactory.success("58b7ed06d024", "item-attribute-value-created", itemAttributeValueInfo));
    }

    @Override
    public ResponseEntity update(@PathVariable final UUID uuid,
                                 @RequestBody @Valid final ItemAttributeValueCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024",
            "item-attribute-value-updated", service.update(uuid, form)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.delete(uuid);
        return Response.ok(respFactory.success("58b7ed06d024", "item-attribute-value-deleted"));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter, final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("17b7eg07d019", "item-attribute-value-filtered", list));
    }

}
