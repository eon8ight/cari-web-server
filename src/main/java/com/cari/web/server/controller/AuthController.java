package com.cari.web.server.controller;

import com.cari.web.server.dto.ClientRequestEntity;
import com.cari.web.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/login")
    public String login(@RequestBody ClientRequestEntity clientRequestEntity) {
        return authService.login(clientRequestEntity);
    }

    @PostMapping("/auth/register")
    public String register(@RequestBody ClientRequestEntity clientRequestEntity) {
        return authService.register(clientRequestEntity);
    }
}
