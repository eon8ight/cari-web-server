package com.cari.web.server.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.domain.db.MessageTemplate;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.dto.response.CariFieldError;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.SendgridService;
import com.cari.web.server.service.UserService;
import com.sendgrid.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {



    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SendgridService sendgridService;

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public AuthResponse register(HttpServletRequest request,
            ClientRequestEntity clientRequestEntity) {
        String emailAddress = clientRequestEntity.getEmailAddress();
        String username = clientRequestEntity.getUsername();
        List<CariFieldError> fieldErrors = new ArrayList<>(2);

        Optional<Entity> entityWithEmailAddress = entityRepository.findByEmailAddress(emailAddress);

        if (entityWithEmailAddress.isEmpty()) {
            fieldErrors.add(new CariFieldError("emailAddress", "Email address is already in use."));
        }

        Optional<Entity> entityWithUsername = entityRepository.findByUsername(username);

        if (entityWithUsername.isEmpty()) {
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
            .created(Timestamp.from(Instant.now()))
            .build();
        // @formatter:on

        entity = entityRepository.save(entity);

        Response response = sendgridService.sendConfirmAccountEmail(request, entity,
                MessageTemplate.CONFIRM_ACCOUNT);

        return response.getStatusCode() >= 400 ? AuthResponse.failure(response.getBody())
                : AuthResponse.success();
    }

    @Override
    public AuthResponse confirm(int pkEntity) {
        Optional<Entity> entityOptional = entityRepository.findById(pkEntity);

        if (entityOptional.isEmpty()) {
            return AuthResponse.failure("Entity with PK " + pkEntity + " does not exist!");
        }

        Entity entity = entityOptional.get();
        entity.setConfirmed(Timestamp.from(Instant.now()));
        entityRepository.save(entity);

        return AuthResponse.success();
    }

    @Override
    public AuthResponse resetPassword(int pkEntity, String password) {
        Optional<Entity> entityOptional = entityRepository.findById(pkEntity);

        if (entityOptional.isEmpty()) {
            return AuthResponse.failure("Entity with PK " + pkEntity + " does not exist!");
        }

        Entity entity = entityOptional.get();
        entity.setPasswordHash(passwordEncoder.encode(password));
        entityRepository.save(entity);

        return AuthResponse.success();
    }
}
