package com.minetec.backend.security;

import com.minetec.backend.entity.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * @author Sinan
 */
public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        final var user = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return Optional.of(user.getId());
    }

}
