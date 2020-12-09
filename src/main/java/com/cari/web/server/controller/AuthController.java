package com.cari.web.server.controller;

import java.time.Duration;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.AuthResponse;
import com.cari.web.server.dto.response.CheckedTokenResponse;
import com.cari.web.server.enums.RequestStatus;
import com.cari.web.server.enums.TokenType;
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

    @Autowired
    private JwtProvider jwtProvider;

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
                .sameSite("None")
                .secure(true)
                .build();
            // @formatter:on

            response = ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                    .body(res);
        }

        return response;
    }

    @GetMapping("/auth/checkSession")
    public ResponseEntity<CheckedTokenResponse> checkSession(HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        CheckedTokenResponse response = authentication.getPrincipal() instanceof User
                ? CheckedTokenResponse
                        .valid(jwtProvider.extractClaims(jwtProvider.resolveToken(request).get()))
                : CheckedTokenResponse.invalid();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/auth/checkToken")
    public ResponseEntity<CheckedTokenResponse> checkToken(@RequestParam("token") String token,
            @RequestParam("type") TokenType type) {
        boolean tokenValid = authService.checkToken(token, type);

        CheckedTokenResponse response =
                tokenValid ? CheckedTokenResponse.valid(jwtProvider.extractClaims(token))
                        : CheckedTokenResponse.invalid();

        return new ResponseEntity<>(response, HttpStatus.OK);
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
