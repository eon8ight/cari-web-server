package com.cari.web.server.config;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import com.cari.web.server.domain.db.Role;
import com.cari.web.server.domain.db.Route;
import com.cari.web.server.repository.RouteRepository;
import com.cari.web.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${management.endpoints.web.cors.allowed-origins}")
    private List<String> corsAllowedOrigins;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserService userDetailsService;

    @Autowired
    private RouteRepository routeRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<Route> routePermissions = routeRepository.findAll();

        Customizer<ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry> authorizeRequestsCustomizer =
                authorizeRequests -> {
                    routePermissions.forEach(route -> {
                        List<Integer> roles = new LinkedList<>(route.getRoles());
                        roles.add(Role.ADMIN);

                        List<String> rolesStr = roles.stream().map(r -> Integer.toString(r))
                                .collect(Collectors.toList());

                        HttpMethod method = HttpMethod.valueOf(route.getHttpMethodLabel());

                        authorizeRequests.antMatchers(method, route.getUrl())
                                .hasAnyAuthority(rolesStr.toArray(new String[rolesStr.size()]));
                    });

                    authorizeRequests.anyRequest().permitAll();
                };

        // @formatter:off
        http
            .cors()
                .and()
            .csrf()
                .disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests(authorizeRequestsCustomizer)
            .exceptionHandling()
                .accessDeniedHandler((request, response, accessDeniedException) -> response
                    .sendError(401, accessDeniedException.getLocalizedMessage()))
                .and()
            .apply(new JwtFilterConfigurer(jwtProvider));
        // @formatter:on
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.debug("CORS allowed origins: " + corsAllowedOrigins);

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsAllowedOrigins);
        configuration.setAllowCredentials(true);

        configuration.setAllowedMethods(
                Arrays.asList(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(),
                        HttpMethod.PUT.name(), HttpMethod.PATCH.name(), HttpMethod.DELETE.name()));
        configuration
                .setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));

        configuration.setExposedHeaders(Arrays.asList("x-auth-token"));
        configuration.setMaxAge(Duration.ofHours(1));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
        impl.setPasswordEncoder(passwordEncoder());
        impl.setUserDetailsService(userDetailsService);
        impl.setHideUserNotFoundExceptions(false);
        return impl;
    }
}
