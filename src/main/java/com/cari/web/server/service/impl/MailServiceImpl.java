package com.cari.web.server.service.impl;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.domain.db.MessageTemplate;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.repository.EntityRepository;
import com.cari.web.server.service.MailService;
import com.cari.web.server.service.SendgridService;
import com.sendgrid.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private SendgridService sendgridService;

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public AuthResponse sendForgotPasswordEmail(HttpServletRequest request,
            ClientRequestEntity clientRequestEntity) {
        String username = clientRequestEntity.getUsername();
        Optional<Entity> entity = entityRepository.findByUsernameOrEmailAddress(username);

        if (entity.isEmpty()) {
            return AuthResponse.failure("Username or email address " + username
                    + " is not associated with an account.");
        }

        Response response = sendgridService.sendForgotPasswordEmail(request, entity.get(),
                MessageTemplate.RESET_PASSWORD);

        return response.getStatusCode() >= 400 ? AuthResponse.failure(response.getBody())
                : AuthResponse.success();
    }
}
