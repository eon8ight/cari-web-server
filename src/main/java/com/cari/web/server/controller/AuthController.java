package com.cari.web.server.controller;

import java.time.Duration;
import java.util.Optional;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody ClientRequestEntity clientRequestEntity) {
        AuthResponse res = authService.login(clientRequestEntity);
        ResponseEntity<AuthResponse> response;

        if (res.getStatus().equals(RequestStatus.FAILURE)) {
            response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
        } else {
            // @formatter:off
            ResponseCookie tokenCookie = ResponseCookie
                .from("sessionToken", res.getToken().get())
                .maxAge(clientRequestEntity.isRememberMe() ? Duration.ofDays(14).toSeconds() : -1)
                .path("/")
                .httpOnly(true)
                .build();
            // @formatter:on

            response = ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                    .body(res);
        }

        return response;
    }

    @GetMapping("/auth/checkSession")
    public ResponseEntity<Boolean> checkSession() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return new ResponseEntity<>(authentication.getPrincipal() instanceof User, HttpStatus.OK);
    }

    @GetMapping("/auth/checkToken")
    public ResponseEntity<Boolean> checkToken(@RequestParam("token") String token) {
        boolean tokenValid = authService.checkToken(token);
        return new ResponseEntity<>(tokenValid, HttpStatus.OK);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<AuthResponse> logout() {
        // @formatter:off
        ResponseCookie tokenCookie = ResponseCookie
            .from("sessionToken", "")
            .maxAge(Duration.ZERO)
            .path("/")
            .httpOnly(true)
            .build();
        // @formatter:on

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .body(AuthResponse.success(Optional.empty()));
    }
}
