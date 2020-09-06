package com.minetec.backend.dto.form;

import lombok.Data;

@Data
public class LoginForm {
    private String email;
    private String password;

    /**
     * @return
     */
    public boolean isEmpty() {
        return email.isEmpty() || password.isEmpty();
    }
}
