package com.minetec.backend.controller;


import com.minetec.backend.dto.form.ThrowCreateForm;
import com.minetec.backend.error_handling.exception.FrontEndException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/throw")
public class FrontEndExceptionController {

    @PostMapping(value = {"", "/"})
    public void feThrow(@RequestBody @Valid final ThrowCreateForm form) {
        throw new FrontEndException("12b5kd01c014", form.getData());
    }
}

