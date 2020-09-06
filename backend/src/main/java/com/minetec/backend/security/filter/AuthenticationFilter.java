package com.minetec.backend.security.filter;

import com.minetec.backend.error_handling.ErrorHandlingControllerAdvice;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class AuthenticationFilter extends GenericFilterBean {

    private final SecurityConstants securityConstants;
    private final ErrorHandlingControllerAdvice errorHandlingControllerAdvice;

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) {

        final var request = (HttpServletRequest) servletRequest;
        final var response = (HttpServletResponse) servletResponse;

        try {

            authenticate(request, checkHeader(request));

            filterChain.doFilter(servletRequest, servletResponse);

        } catch (ExpiredJwtException e) {
            errorHandlingControllerAdvice.handleException(e, request, response);
        } catch (Exception e) {
            errorHandlingControllerAdvice.defaultExceptionHandler(e, request, response);
        }
    }

    /**
     * @param request
     * @param headerToken
     */
    private void authenticate(final HttpServletRequest request, final String headerToken) {

        UUID userId = null;

        if (headerToken != null) {
            Claims body = Jwts.parser().setAllowedClockSkewSeconds(securityConstants.getExpirationTime())
                .setSigningKey(securityConstants.getSecretKey().getBytes()).parseClaimsJws(headerToken).getBody();
            if (body != null && body.get("sub") != null) {
                userId = UUID.fromString(body.get("sub").toString());
            }
        }

        request.setAttribute("userId", userId);
    }

    /**
     * @param request
     * @return
     */
    private String checkHeader(final HttpServletRequest request) {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer")) {
            return authHeader.replace("Bearer ", "");
        }

        return null;
    }
}
