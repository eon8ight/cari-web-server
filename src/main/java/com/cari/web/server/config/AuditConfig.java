package com.cari.web.server.config;

import com.cari.web.server.util.CariAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

@Configuration
@EnableJdbcAuditing
public class AuditConfig {

    @Bean
    public AuditorAware<Integer> auditorProvider() {
        return new CariAuditorAware();
    }
}
