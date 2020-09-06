package com.minetec.backend.controller;

import com.minetec.backend.dto.form.UserCreateUpdateForm;
import com.minetec.backend.service.UserService;
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
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Secured(value = {"ROLE_ADMIN"})
public class UserController extends BaseController<UserService, UserCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final UserCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024", "user-created", service.create(form)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.softDelete(uuid);
        return Response.ok(respFactory.success("58b7ed06d024", "user-deleted"));
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("58b7ed06d024", "user-fetch", service.find(uuid)));
    }

    @Override
    public ResponseEntity update(@RequestParam final UUID uuid, @RequestBody @Valid final UserCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024", "user-updated", service.update(uuid, form)));
    }

    @Override
    public ResponseEntity list(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("58b7ed06d024", "user-listed", service.list(pageable)));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter, final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("17b7eg07d019", "user-filtered", list));
    }

    @GetMapping("/find-position/{group}")
    public ResponseEntity findPosition(@PathVariable @NotNull final String group, final Pageable pageable) {
        final var list = service.findBy(group, pageable);
        return Response.ok(respFactory.success("17b7eg07d019", "user-filtered", list));
    }

}
