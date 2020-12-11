package com.cari.web.server.service;

import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.enums.TokenType;
import com.cari.web.server.exception.LoginException;

public interface AuthService {

    String login(ClientRequestEntity clientRequestEntity) throws LoginException;

    boolean checkToken(String token, TokenType type);
}
