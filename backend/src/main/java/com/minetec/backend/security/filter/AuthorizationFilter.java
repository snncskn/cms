package com.minetec.backend.security.filter;

import com.minetec.backend.dto.AuthorizationState;
import com.minetec.backend.entity.Role;
import com.minetec.backend.error_handling.ErrorHandlingControllerAdvice;
import com.minetec.backend.error_handling.exception.AccessNotAllowedException;
import com.minetec.backend.service.UserService;
import com.minetec.backend.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class AuthorizationFilter extends GenericFilterBean {

    private final UserService userService;
    private final ErrorHandlingControllerAdvice errorHandlingControllerAdvice;

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) {

        final var request = (HttpServletRequest) servletRequest;
        final var response = (HttpServletResponse) servletResponse;

        try {
            final var userId = servletRequest.getAttribute("userId");

            if (userId == null) { // Guest access
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }

            final var currentUser = userService.findByUuid(UUID.fromString(userId.toString()));

            if (currentUser.isLocked()) {
                throw new AccessNotAllowedException("e18864a666af", "user-is-locked");
            }

            if (Utils.isEmpty(currentUser.getSites())) {
                throw new AccessNotAllowedException("d18264a366ak", "site-not-defined");
            }

            var forName = new StringBuilder(currentUser.getFirstName());
            forName.append(" ").append(currentUser.getLastName());

            final var site = request.getHeader("Site");

            final AuthorizationState authorizationState = AuthorizationState.builder()
                .authenticated(true)
                .name(forName.toString())
                .principal(currentUser)
                .details(AuthorizationDetail.builder().siteUuid(UUID.fromString(site)).build())
                .authorities(getAuthorities(currentUser.getRoles()))
                .build();

            SecurityContextHolder.getContext().setAuthentication(authorizationState);

            filterChain.doFilter(servletRequest, servletResponse);

        } catch (ServletException | IOException e) {
            errorHandlingControllerAdvice.defaultExceptionHandler(e, request, response);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(roleInfo -> authorities.add(new SimpleGrantedAuthority(roleInfo.getName())));
        return authorities;
    }
}
