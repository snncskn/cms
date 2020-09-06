package com.minetec.backend.controller;

import com.minetec.backend.dto.RestResponse;
import com.minetec.backend.dto.form.VehicleAttributeCreateUpdateForm;
import com.minetec.backend.service.VehicleAttributeService;
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
@RequestMapping("/api/vehicle-attribute")
@Secured(value = {"ROLE_ADMIN", "ROLE_ENGINEERING_PLANNER"})
public class VehicleAttributeController
    extends BaseController<VehicleAttributeService, VehicleAttributeCreateUpdateForm> {


    @Override
    public ResponseEntity list(
        @PageableDefault(sort = "name", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(
            respFactory.success("58b7ed06d024", "vehicle-attribute-listed", service.list(pageable)));
    }

    @Override
    public ResponseEntity create(@RequestBody @Valid final VehicleAttributeCreateUpdateForm form) {
        return Response.ok(
            respFactory.success("58b7ed06d024", "vehicle-attribute-created", service.create(form)));
    }

    @Override
    public ResponseEntity update(@PathVariable final UUID uuid,
                                 @RequestBody @Valid final VehicleAttributeCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024",
            "vehicle-attribute-updated", service.update(uuid, form)));
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(
            respFactory.success("58b7ed06d024", "vehicle-attribute-fetched", service.find(uuid)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.delete(uuid);
        return Response.ok(respFactory.success("58b7ed06d024", "vehicle-attribute-deleted"));
    }

    @Override
    public ResponseEntity<RestResponse> filter(@PathVariable final String filter,
                                               @PageableDefault(sort = "name", direction = Sort.Direction.ASC)
                                               final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("28b7ed07e084", "vehicle-attribute-value-filtered", list));
    }
}
