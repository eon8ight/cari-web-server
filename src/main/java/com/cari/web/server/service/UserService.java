package com.cari.web.server.service;

import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.request.ClientRequestToken;
import com.cari.web.server.dto.response.AuthResponse;

public interface UserService {

    AuthResponse register(HttpServletRequest request, ClientRequestEntity clientRequestEntity);

    AuthResponse confirm(ClientRequestToken token);
}
