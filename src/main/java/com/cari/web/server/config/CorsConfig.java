package com.cari.web.server.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${management.endpoints.web.cors.allowed-origins}")
    private List<String> corsAllowedOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                System.out.println(corsAllowedOrigins.get(0));
                registry.addMapping("/**")
                        .allowedOrigins(corsAllowedOrigins.toArray(new String[] {}));
            }
        };
    }
}

