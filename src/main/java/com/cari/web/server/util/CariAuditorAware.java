package com.cari.web.server.util;

import java.util.Optional;
import com.cari.web.server.domain.db.Entity;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class CariAuditorAware implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        Entity principal = (Entity) authentication.getPrincipal();
        return Optional.of(principal.getEntity());
    }
}
