package com.cari.web.server.controller;

import java.time.Duration;
import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.config.JwtProvider;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.dto.request.ClientRequestEntity;
import com.cari.web.server.dto.response.CariResponse;
import com.cari.web.server.dto.response.CheckedTokenResponse;
import com.cari.web.server.enums.TokenType;
import com.cari.web.server.exception.LoginException;
import com.cari.web.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseCookie.ResponseCookieBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Value("${spring.environment:local}")
    private String springEnvironment;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProvider jwtProvider;

    private ResponseCookie buildTokenCookie(String token, long maxAge) {
        // @formatter:off
        ResponseCookieBuilder tokenCookieBuilder = ResponseCookie
            .from("sessionToken", token)
            .maxAge(maxAge)
            .path("/")
            .httpOnly(true);
        // @formatter:on

        if (!springEnvironment.equals("local")) {
            tokenCookieBuilder.sameSite("None").secure(true);
        }

        return tokenCookieBuilder.build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<CariResponse> login(
            @RequestBody ClientRequestEntity clientRequestEntity) {
        String jwt;

        try {
            jwt = authService.login(clientRequestEntity);
        } catch (LoginException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CariResponse.failure(ex.getErrors()));
        }

        ResponseCookie tokenCookie = buildTokenCookie(jwt,
                clientRequestEntity.isRememberMe() ? Duration.ofDays(14).toSeconds() : -1);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .body(CariResponse.success());
    }

    @GetMapping("/auth/checkSession")
    public ResponseEntity<CheckedTokenResponse> checkSession(HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        CheckedTokenResponse response = authentication.getPrincipal() instanceof Entity
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
    public ResponseEntity<CariResponse> logout() {
        ResponseCookie tokenCookie = buildTokenCookie("", Duration.ZERO.toSeconds());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, tokenCookie.toString())
                .body(CariResponse.success());
    }
}
