package com.cari.web.server.controller;

import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody ClientRequestEntity clientRequestEntity) {
        AuthResponse res = userService.register(clientRequestEntity);
        HttpStatus status = res.getStatus().equals(RequestStatus.FAILURE) ? HttpStatus.FORBIDDEN
                : HttpStatus.OK;

        return new ResponseEntity<>(res, status);
    }
}
