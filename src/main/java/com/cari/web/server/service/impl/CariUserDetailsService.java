package com.cari.web.server.service.impl;

import java.util.Optional;
import com.cari.web.server.domain.Entity;
import com.cari.web.server.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CariUserDetailsService implements UserDetailsService {

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Entity entity = entityRepository.findByUsernameOrEmailAddress(username);

        if (entity == null) {
            throw new UsernameNotFoundException(
                    "User with username or email address \"" + username + "\" does not exist");
        }

        return entity.toUserDetails();
    }

    public UserDetails loadByEntity(int pkEntity) {
        Optional<Entity> entity = entityRepository.findById(pkEntity);

        if(!entity.isPresent()) {
            throw new UsernameNotFoundException("Entity with ID " + pkEntity + " does not exist!");
        };

        return entity.get().toUserDetails();
    }
}
