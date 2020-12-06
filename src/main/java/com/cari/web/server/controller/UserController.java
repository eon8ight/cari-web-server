package com.cari.web.server.controller;

import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.request.ClientRequestToken;
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
    public ResponseEntity<AuthResponse> register(HttpServletRequest request,
            @RequestBody ClientRequestEntity clientRequestEntity) {
        AuthResponse res = userService.register(request, clientRequestEntity);
        HttpStatus status = res.getStatus().equals(RequestStatus.FAILURE) ? HttpStatus.FORBIDDEN
                : HttpStatus.OK;

        return new ResponseEntity<>(res, status);
    }

    @PostMapping("/user/confirm")
    public ResponseEntity<AuthResponse> confirm(@RequestBody ClientRequestToken token) {
        AuthResponse res = userService.confirm(token);
        HttpStatus status = res.getStatus().equals(RequestStatus.FAILURE) ? HttpStatus.FORBIDDEN
                : HttpStatus.OK;

        return new ResponseEntity<>(res, status);
    }
}