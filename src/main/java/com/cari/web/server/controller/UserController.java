package com.cari.web.server.controller;

import java.util.Map;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
public class UserController {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody ClientRequestEntity clientRequestEntity) {
        String token = clientRequestEntity.getToken();
        AuthResponse res;

        try {
            jwtProvider.validateInviteToken(token);
            int pkEntity = jwtProvider.getEntityFromToken(token);
            res = userService.register(pkEntity, clientRequestEntity);
        } catch (HttpClientErrorException ex) {
            res = AuthResponse.failure("Token is invalid");
        }

        HttpStatus status = res.getStatus().equals(RequestStatus.FAILURE) ? HttpStatus.BAD_REQUEST
                : HttpStatus.OK;

        return new ResponseEntity<>(res, status);
    }

    @PostMapping("/user/confirm")
    public ResponseEntity<AuthResponse> confirm(
            @RequestBody ClientRequestEntity clientRequestEntity) {
        String token = clientRequestEntity.getToken();
        AuthResponse res;

        try {
            jwtProvider.validateConfirmToken(token);
            int pkEntity = jwtProvider.getEntityFromToken(token);
            res = userService.confirm(pkEntity);
        } catch (HttpClientErrorException ex) {
            res = AuthResponse.failure("Token is invalid");
        }

        HttpStatus status = res.getStatus().equals(RequestStatus.FAILURE) ? HttpStatus.FORBIDDEN
                : HttpStatus.OK;

        return new ResponseEntity<>(res, status);
    }

    @PostMapping("/user/resetPassword")
    public ResponseEntity<AuthResponse> resetPassword(
            @RequestBody ClientRequestEntity clientRequestEntity) {
        String token = clientRequestEntity.getToken();
        AuthResponse res;

        try {
            jwtProvider.validateResetPasswordToken(token);
            int pkEntity = jwtProvider.getEntityFromToken(token);
            res = userService.resetPassword(pkEntity, clientRequestEntity.getPassword());
        } catch (HttpClientErrorException ex) {
            res = AuthResponse.failure("Token is invalid");
        }

        HttpStatus status = res.getStatus().equals(RequestStatus.FAILURE) ? HttpStatus.FORBIDDEN
                : HttpStatus.OK;

        return new ResponseEntity<>(res, status);
    }

    @GetMapping("/user/findForList")
    public Page<Entity> findForList(@RequestParam Map<String, String> filters) {
        return userService.findForList(filters);
    }

    @PostMapping("/user/invite")
    public ResponseEntity<AuthResponse> invite(
            @RequestBody ClientRequestEntity clientRequestEntity) {
        AuthResponse res = userService.invite(clientRequestEntity);

        HttpStatus status = res.getStatus().equals(RequestStatus.FAILURE) ? HttpStatus.BAD_REQUEST
                : HttpStatus.OK;

        return new ResponseEntity<>(res, status);
    }
}
