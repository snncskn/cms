package com.minetec.backend.controller;

import com.minetec.backend.dto.form.RoleCreateUpdateForm;
import com.minetec.backend.service.RoleService;
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
 * @author Brandon Tabe
 */

@RestController
@RequestMapping("/api/role")
@Secured(value = {"ROLE_ADMIN"})
public class RoleController extends BaseController<RoleService, RoleCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final RoleCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ek06d024", "role-save", service.create(form)));
    }

    @Override
    public ResponseEntity update(@RequestParam final UUID uuid, @RequestBody @Valid final RoleCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024", "role-save", service.update(uuid, form)));
    }

    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("28b8ed06d024", "role-list", service.find(uuid)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.delete(uuid);
        return Response.ok(respFactory.success("18b7ed06u024", "role-delete"));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter, final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("28b7ed07e184", "role-filtered", list));
    }

    @Override
    public ResponseEntity list(
        @PageableDefault(sort = "id", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("58b7ed06d094", "role-list", service.list(pageable)));
    }
}
