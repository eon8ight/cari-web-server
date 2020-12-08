package com.cari.web.server.controller;

import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {

    @Autowired
    private MailService mailService;

    @PostMapping("/mail/forgotPassword")
    public ResponseEntity<AuthResponse> forgotPassword(HttpServletRequest request,
            @RequestBody ClientRequestEntity clientRequestEntity) {
        AuthResponse res = mailService.sendForgotPasswordEmail(request, clientRequestEntity);

        HttpStatus status = res.getStatus().equals(RequestStatus.FAILURE) ? HttpStatus.BAD_REQUEST
                : HttpStatus.OK;

        return new ResponseEntity<>(res, status);
    }
}
