package com.cari.web.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;

@Configuration
public class JdbcConfig {

    @Bean
    public NamingStrategy getNamingStrategy() {
        return new NamingStrategy() {
            @Override
            public String getKeyColumn(RelationalPersistentProperty property) {
                return property.getOwner().getTableName().getReference().replaceAll("^tb_", "");
            }
        };
    }
}
