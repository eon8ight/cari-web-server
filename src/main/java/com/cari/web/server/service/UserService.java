package com.cari.web.server.service;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import org.springframework.data.domain.Page;

public interface UserService {

    AuthResponse register(HttpServletRequest request, int pkEntity,
            ClientRequestEntity clientRequestEntity);

    AuthResponse confirm(int pkEntity);

    AuthResponse resetPassword(int pkEntity, String password);

    Page<Entity> findForList(Map<String, String> filters);

    AuthResponse invite(HttpServletRequest request, ClientRequestEntity clientRequestEntity);
}
