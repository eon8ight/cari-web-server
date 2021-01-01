package com.cari.web.server.service.impl;

import java.util.Optional;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.domain.CariFieldError;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.enums.TokenType;
import com.cari.web.server.exception.LoginException;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
    public String login(ClientRequestEntity clientRequestEntity) throws LoginException {
        String username = clientRequestEntity.getUsername();
        CariFieldError fieldError;

        try {
            Optional<Entity> entityOptional =
                    entityRepository.findByUsernameOrEmailAddress(username);

            if (entityOptional.isPresent()) {
                Entity entity = entityOptional.get();

                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,
                        clientRequestEntity.getPassword(), entity.getAuthorities()));

                return jwtProvider.createSessionToken(entity);
            }

            fieldError = new CariFieldError(FIELD_USERNAME,
                    "Entity with username " + username + " could not be found.");
        } catch (AuthenticationException ex) {
            String message = ex.getLocalizedMessage();
            String field = FIELD_USERNAME;

            switch (message) {
                case "User is disabled":
                    message = "You have not yet confirmed your account.";
                    field = FIELD_USERNAME;
                    break;
                case "Bad credentials":
                    message = "Password is incorrect.";
                    field = FIELD_PASSWORD;
                    break;
            }

            fieldError = new CariFieldError(field, message);
        }

        throw new LoginException(fieldError);
    }

    @Override
    public boolean checkToken(String token, TokenType type) {
        try {
            jwtProvider.validateToken(token, type);
        } catch (HttpClientErrorException ex) {
            return false;
        }

        return true;
    }
}
