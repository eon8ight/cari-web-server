package com.cari.web.server.service.impl;

import java.util.Arrays;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.domain.Entity;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public AuthResponse login(ClientRequestEntity clientRequestEntity) {
        String username = clientRequestEntity.getUsername();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,
                    clientRequestEntity.getPassword(),
                    Arrays.asList(new SimpleGrantedAuthority(Entity.ROLE_USER))));

            Entity entity = entityRepository.findByUsernameOrEmailAddress(username);
            String jwt = jwtProvider.createToken(entity);
            return AuthResponse.success(jwt);
        } catch (AuthenticationException ex) {
            String message = ex.getLocalizedMessage();

            if (message.equals("Bad credentials")) {
                message = "Password is incorrect.";
            }

            return AuthResponse.failure(message);
        }
    }

    @Override
    public AuthResponse register(ClientRequestEntity clientRequestEntity) {
        String emailAddress = clientRequestEntity.getEmailAddress();
        String username = clientRequestEntity.getUsername();

        Entity entityWithEmailAddress = entityRepository.findByEmailAddress(emailAddress);

        if (entityWithEmailAddress != null) {
            return AuthResponse.failure("Email address is already in use.");
        }

        Entity entityWithUsername = entityRepository.findByUsername(username);

        if (entityWithUsername != null) {
            return AuthResponse.failure("Username is already in use.");
        }

        // @formatter:off
        Entity entity = Entity.builder()
            .username(username)
            .emailAddress(emailAddress)
            .passwordHash(passwordEncoder.encode(clientRequestEntity.getPassword()))
            .build();
        // @formatter:on

        entityRepository.save(entity);
        String jwt = jwtProvider.createToken(entity);

        return AuthResponse.success(jwt);
    }
}
