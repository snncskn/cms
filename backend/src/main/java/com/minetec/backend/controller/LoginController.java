package com.minetec.backend.controller;

import com.minetec.backend.dto.form.LoginForm;
import com.minetec.backend.error_handling.exception.AccessNotAllowedException;
import com.minetec.backend.service.AuthenticationService;
import com.minetec.backend.util.Response;
import com.minetec.backend.util.RestResponseFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Sinan
 */

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final RestResponseFactory respFactory;

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity authenticateUser(@RequestBody  @Valid @NotNull final LoginForm form) {

        log.debug("Api method authenticateUser called");

        // Check if any authentication information has been given
        if (form == null && form.isEmpty()) {
            throw new AccessNotAllowedException("8cd06beb0239", "form-not-found");
        }

        // Retrieve the user token once validated
        final var userInfo = authenticationService.authenticateLoginCredentials(form);

        // Send back a successful response with the token included
        return Response.ok(respFactory.success("2ca08beb0132", "retrieve-token", userInfo));

    }
}
