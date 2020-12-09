package com.cari.web.server.service;

import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.domain.db.Entity;
import com.sendgrid.Response;

public interface SendgridService {

    Response sendConfirmAccountEmail(HttpServletRequest request, Entity entity);

    Response sendForgotPasswordEmail(HttpServletRequest request, Entity entity);

    Response sendInviteEmail(HttpServletRequest request, Entity fromEntity, Entity entity);
}