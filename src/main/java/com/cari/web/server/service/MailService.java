package com.cari.web.server.service;

import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;

public interface MailService {

    AuthResponse sendForgotPasswordEmail(ClientRequestEntity clientRequestEntity);
}
