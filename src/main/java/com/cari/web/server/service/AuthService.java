package com.cari.web.server.service;

import com.cari.web.server.dto.ClientRequestEntity;

public interface AuthService {

    String login(ClientRequestEntity clientRequestEntity);

    String register(ClientRequestEntity clientRequestEntity);
}
