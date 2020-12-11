package com.cari.web.server.service;

import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.CariResponse;

public interface MailService {

    CariResponse sendForgotPasswordEmail(ClientRequestEntity clientRequestEntity);
}
