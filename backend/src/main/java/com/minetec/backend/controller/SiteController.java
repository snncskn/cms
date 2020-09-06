package com.minetec.backend.controller;

import com.minetec.backend.dto.form.SiteCreateUpdateForm;
import com.minetec.backend.service.SiteService;
import com.minetec.backend.util.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * @author Sandhya Mengani
 */

@RestController
@RequestMapping("/api/site")
public class SiteController extends BaseController<SiteService, SiteCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final SiteCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024", "site-save", service.create(form)));
    }

    @Override
    public ResponseEntity update(@PathVariable final UUID uuid, @RequestBody @Valid final SiteCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024", "site-save", service.update(uuid, form)));
    }

    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("58b7ed06d024", "site-list", service.find(uuid)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.delete(uuid);
        return Response.ok(respFactory.success("58b7ed06d024", "site-delete"));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter, final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("28b7ed07e084", "site-filtered", list));
    }
    @Override
    public ResponseEntity list(
        @PageableDefault(sort = "description", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("58b7ed06d024", "site-list", service.list(pageable)));
    }
}
