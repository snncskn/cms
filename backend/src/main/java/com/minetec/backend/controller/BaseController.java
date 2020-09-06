package com.minetec.backend.controller;

import com.minetec.backend.util.RestResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

/**
 * @param <S>
 * @param <T>
 * @author Sinan
 * <p>
 * <p>
 * For Backend:
 * <p>
 * GET     /api/some-resource/{uuid}               Get Detail of single resource
 * POST    /api/some-resource                      Create resource with incoming data
 * PUT     /api/some-resource/{uuid}               Update resource with incoming data
 * DELETE  /api/some-resource/{uuid}               Delete resource
 * GET     /api/some-resources                     List all resources with post data
 * <p>
 * XXXX    /api/some-resource/{uuid}/some-action   Perform some action for that specified resource
 * XXXX    /api/some-resources/some-action         Perform some action for all data
 */

@CrossOrigin
public abstract class BaseController<S, T> {

    @Autowired
    public S service;

    @Autowired
    public RestResponseFactory respFactory;

    @GetMapping(value = {"", "/"})
    public abstract ResponseEntity list(Pageable pageable);

    @GetMapping(value = {"/{uuid}"})
    public abstract ResponseEntity fetch(UUID uuid);

    @PostMapping(value = {"", "/"})
    public abstract ResponseEntity create(T form);

    @PutMapping(value = {"", "/"})
    public abstract ResponseEntity update(UUID uuid, T form);

    @DeleteMapping(value = {"", "/"})
    public abstract ResponseEntity delete(UUID uuid);

    @GetMapping(value = {"/filter/{filter}"})
    public abstract ResponseEntity filter(String filter, Pageable pageable);

}
