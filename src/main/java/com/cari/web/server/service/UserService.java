package com.cari.web.server.service;

import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;

public interface UserService {

    AuthResponse register(HttpServletRequest request, ClientRequestEntity clientRequestEntity);

    AuthResponse confirm(int pkEntity);

    AuthResponse resetPassword(int pkEntity, String password);
}
