package com.minetec.backend.controller;

import com.minetec.backend.service.FieldAttributeService;
import com.minetec.backend.util.Response;
import com.minetec.backend.util.RestResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sinan
 */

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/field-attribute")
@Secured(value = {"ROLE_ADMIN"})
public class FieldAttributeController {

    private final FieldAttributeService fieldAttributeService;
    private final RestResponseFactory respFactory;

    @GetMapping("/find-field/{fieldId}")
    public ResponseEntity findField(@PathVariable final String fieldId) {
        return Response.ok(respFactory.success("46b7ed05d019", "find-field",
            fieldAttributeService.findByField(fieldId)));
    }

}
