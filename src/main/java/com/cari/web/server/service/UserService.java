package com.cari.web.server.service;

import java.util.Map;
import java.util.Optional;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.dto.response.UserInviteResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    Optional<Entity> find(int pkEntity);

    CariResponse register(int pkEntity, ClientRequestEntity clientRequestEntity);

    CariResponse confirm(int pkEntity);

    CariResponse resetPassword(int pkEntity, String password);

    Page<Entity> findForList(Map<String, String> filters);

    UserInviteResponse invite(ClientRequestEntity clientRequestEntity);

    Entity findForEdit();

    CariResponse edit(ClientRequestEntity clientRequestEntity);
}
