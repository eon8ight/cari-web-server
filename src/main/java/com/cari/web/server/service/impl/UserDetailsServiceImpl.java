package com.cari.web.server.service.impl;

import com.cari.web.server.domain.Entity;
import com.cari.web.server.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Entity entity = entityRepository.findByUsername(username);

        if (entity == null) {
            throw new UsernameNotFoundException(
                    "User with username or email address \"" + username + "\" does not exist");
        }

        // @formatter:off
        return User
            .withUsername(username)
            .password(entity.getPasswordHash())
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .roles(Entity.ROLE_USER)
            .build();
        // @formatter:on
    }

}
