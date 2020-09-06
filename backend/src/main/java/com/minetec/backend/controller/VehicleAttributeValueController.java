package com.minetec.backend.controller;

import com.minetec.backend.dto.form.VehicleAttributeValueCreateUpdateForm;
import com.minetec.backend.service.VehicleAttributeService;
import com.minetec.backend.service.VehicleAttributeValueService;
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
@RequestMapping("/api/vehicle-attribute-value")
@RequiredArgsConstructor
@Secured(value = {"ROLE_ADMIN", "ROLE_ENGINEERING_PLANNER"})
public class VehicleAttributeValueController
    extends BaseController<VehicleAttributeValueService, VehicleAttributeValueCreateUpdateForm> {

    private final VehicleAttributeService vehicleAttributeService;

    @Override
    public ResponseEntity list(
        @PageableDefault(sort = "value", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("58b7ed06d024", "vehicle-attribute-value-listed",
            service.list(pageable)));
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("58b7ed06d024", "vehicle-attribute-value-fetched",
            service.find(uuid)));
    }

    @Override
    public ResponseEntity create(@RequestBody @Valid final VehicleAttributeValueCreateUpdateForm form) {
        final var vehicleAttribute = vehicleAttributeService.findByUuid(form.getVehicleAttributeUuid());
        final var vehicleAttributeValueInfo = service.create(vehicleAttribute, form);
        final var resp = respFactory.success("58b7ed06d024", "vehicle-attribute-value-created",
            vehicleAttributeValueInfo);
        return Response.ok(resp);
    }

    @Override
    public ResponseEntity update(@PathVariable final UUID uuid,
                                 @RequestBody @Valid final VehicleAttributeValueCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024",
            "vehicle-attribute-value-updated", service.update(uuid, form)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.delete(uuid);
        return Response.ok(respFactory.success("58b7ed06d024", "vehicle-attribute-value-deleted"));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter,
                                 @PageableDefault(sort = "value", direction = Sort.Direction.ASC)
                                 final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("28b7ed07e084", "vehicle-attribute-value-filtered", list));
    }

}
