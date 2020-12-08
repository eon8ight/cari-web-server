package com.cari.web.server.service.impl;

import java.util.Arrays;
import java.util.Optional;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.dto.response.CariFieldError;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String FIELD_USERNAME = "username";
    private static final String FIELD_PASSWORD = "password";

    @Autowired
    private AuthenticationManager authenticationManager;

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

            // If we got this far, an entity with the given username or email address is guaranteed
            // to exist
            Entity entity = entityRepository.findByUsernameOrEmailAddress(username).get();

            if (entity.getConfirmed() == null) {
                return AuthResponse.failure(Arrays.asList(new CariFieldError(FIELD_USERNAME,
                        "You have not yet confirmed your account.")));
            }

            String jwt = jwtProvider.createSessionToken(entity, Optional.empty());

            return AuthResponse.success(Optional.of(jwt));
        } catch (AuthenticationException ex) {
            String message = ex.getLocalizedMessage();
            String field = FIELD_USERNAME;

            if (message.equals("Bad credentials")) {
                message = "Password is incorrect.";
                field = FIELD_PASSWORD;
            }

            CariFieldError fieldError = new CariFieldError(field, message);
            return AuthResponse.failure(Arrays.asList(fieldError));
        }
    }

    @Override
    public boolean checkToken(String token) {
        try {
            jwtProvider.validateAnyToken(token);
        } catch (HttpClientErrorException ex) {
            return false;
        }

        return true;
    }
}
