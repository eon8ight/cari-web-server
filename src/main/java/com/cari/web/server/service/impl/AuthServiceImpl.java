package com.cari.web.server.service.impl;

import java.util.Arrays;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.domain.Entity;
import com.cari.web.server.dto.ClientRequestEntity;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

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
    public String login(ClientRequestEntity clientRequestEntity) {
        String username = clientRequestEntity.getUsername();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,
                    clientRequestEntity.getPassword(),
                    Arrays.asList(new SimpleGrantedAuthority(Entity.ROLE_USER))));

            Entity entity = entityRepository.findByUsername(username);
            return jwtProvider.createToken(entity);
        } catch (AuthenticationException ex) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage());
        }
    }

    @Override
    public String register(ClientRequestEntity clientRequestEntity) {
        String username = clientRequestEntity.getUsername();

        // @formatter:off
        Entity entity = Entity.builder()
            .username(username)
            .emailAddress(clientRequestEntity.getEmailAddress())
            .passwordHash(passwordEncoder.encode(clientRequestEntity.getPassword()))
            .build();
        // @formatter:on

        entityRepository.save(entity);
        return jwtProvider.createToken(entity);
    }
}
