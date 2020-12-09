package com.cari.web.server.service;

import com.cari.web.server.domain.db.Entity;
import com.sendgrid.Response;

public interface SendgridService {

    Response sendConfirmAccountEmail(Entity entity);

    Response sendForgotPasswordEmail(Entity entity);

    Response sendInviteEmail(Entity fromEntity, Entity entity);
}
