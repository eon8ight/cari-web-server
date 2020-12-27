package com.cari.web.server.util;

import java.sql.Timestamp;
import com.cari.web.server.domain.db.Entity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SessionUtils {

    public static Entity getSessionEntity() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return (Entity) authentication.getPrincipal();
    }

    public static Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
}
