package com.cari.web.server.service;

import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.domain.Entity;
import com.sendgrid.Response;

public interface SendgridService {

    Response sendConfirmAccountEmail(HttpServletRequest request, Entity entity,
            int pkMessageTemplate);
}
