package com.cari.web.server.config;

import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        Dotenv dotenv = Dotenv.load();

        String url = new StringBuilder("jdbc:postgresql://")
            .append(dotenv.get("CARI_DB_HOST"))
            .append(":")
            .append(dotenv.get("CARI_DB_PORT"))
            .append("/")
            .append(dotenv.get("CARI_DB_NAME"))
            .toString();

        return DataSourceBuilder.create().url(url).username(dotenv.get("CARI_DB_USER"))
            .password(dotenv.get("CARI_DB_PASSWORD")).build();
    }
}
