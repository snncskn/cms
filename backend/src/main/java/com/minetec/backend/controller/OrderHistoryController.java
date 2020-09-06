package com.minetec.backend.controller;

import com.minetec.backend.dto.form.OrderHistoryCreateUpdateForm;
import com.minetec.backend.service.OrderHistoryService;
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
@RequestMapping("/api/order-history")
@Secured(value = {"ROLE_ADMIN"})
public class OrderHistoryController extends BaseController<OrderHistoryService, OrderHistoryCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final OrderHistoryCreateUpdateForm form) {
        return Response.badRequest().build();
    }

    @Override
    public ResponseEntity update(final UUID uuid, final OrderHistoryCreateUpdateForm form) {
        return Response.badRequest().build();
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        return Response.badRequest().build();
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.badRequest().build();
    }

    @Override
    public ResponseEntity list(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("65b7ec06d027", "order-listed", service.list(pageable)));
    }

    @Override
    public ResponseEntity filter(final String filter, final Pageable pageable) {
        return Response.badRequest().build();
    }

}
