package com.minetec.backend.dto;

import com.minetec.backend.security.filter.AuthorizationDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public final class AuthorizationState implements Authentication {
    private boolean authenticated;
    private Object credentials;
    private AuthorizationDetail details;
    private String name;
    private Object principal;
    private Collection<? extends GrantedAuthority> authorities;
}
