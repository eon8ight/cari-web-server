package com.cari.web.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.domain.Entity;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.dto.response.CariFieldError;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public AuthResponse register(ClientRequestEntity clientRequestEntity) {
        String emailAddress = clientRequestEntity.getEmailAddress();
        String username = clientRequestEntity.getUsername();
        List<CariFieldError> fieldErrors = new ArrayList<>(2);

        Entity entityWithEmailAddress = entityRepository.findByEmailAddress(emailAddress);

        if (entityWithEmailAddress != null) {
            fieldErrors.add(new CariFieldError("emailAddress", "Email address is already in use."));
        }

        Entity entityWithUsername = entityRepository.findByUsername(username);

        if (entityWithUsername != null) {
            fieldErrors.add(new CariFieldError("username", "Username is already in use."));
        }

        if (fieldErrors.size() > 0) {
            return AuthResponse.failure(fieldErrors);
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
