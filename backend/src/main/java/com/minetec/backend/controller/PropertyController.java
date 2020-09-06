package com.minetec.backend.controller;

import com.minetec.backend.dto.form.PropertyCreateUpdateForm;
import com.minetec.backend.service.PropertyService;
import com.minetec.backend.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/property")
@Secured(value = {"ROLE_ADMIN"})
public class PropertyController extends BaseController<PropertyService, PropertyCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final PropertyCreateUpdateForm form) {
        return Response.ok(respFactory.success("18b7eh06d064", "property-save", service.create(form)));
    }

    @Override
    public ResponseEntity update(@RequestParam final UUID uuid,
                                 @RequestBody @Valid final PropertyCreateUpdateForm form) {
        return Response.ok(
                respFactory.success("78b7jd09d028", "property-save", service.update(uuid, form)));
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("38b7kd09d019", "property-list", service.find(uuid)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.delete(uuid);
        return Response.ok(respFactory.success("28b4ed05d084", "property-delete"));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter,
                                 @PageableDefault(sort = "groupName", direction = Sort.Direction.ASC)
                                 final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("28b2eh08ed21", "property-filtered", list));
    }

    @GetMapping(value = {"/findByGroupName/{filter}"})
    public ResponseEntity findByGroupName(@PathVariable final String filter,
                                          @PageableDefault(sort = "groupName", direction = Sort.Direction.ASC)
                                          final Pageable pageable) {
        final var list = service.findByGroupName(filter, pageable);
        return Response.ok(respFactory.success("28b2eh08ed21", "property-filtered", list));
    }

    @Override
    public ResponseEntity list(@PageableDefault(sort = "groupName", direction = Sort.Direction.ASC)
                                                final Pageable pageable) {
        return Response.ok(respFactory.success("73b4gs05d025", "property-list", service.list(pageable)));
    }

}
