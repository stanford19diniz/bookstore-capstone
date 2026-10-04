package com.bookstore.common;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Provides the current authenticated user's email for JPA audit columns
 * (created_by / updated_by). Falls back to "system" for unauthenticated contexts.
 * Registered as a Spring bean via AppConfig — not @Component to avoid duplicate.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return Optional.of("system");
        }
        return Optional.of(auth.getName());
    }
}
