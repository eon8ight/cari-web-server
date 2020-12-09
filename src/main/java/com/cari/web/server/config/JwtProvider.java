package com.cari.web.server.config;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.domain.db.Entity;
import com.cari.web.server.enums.TokenType;
import com.cari.web.server.service.impl.CariUserDetailsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.gson.io.GsonDeserializer;
import io.jsonwebtoken.gson.io.GsonSerializer;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long expireLength;

    @Autowired
    private CariUserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    private Gson createGson() {
        return new GsonBuilder().disableHtmlEscaping().create();
    }

    private JwtParser createJwtParser() {
        Gson gson = createGson();

        // @formatter:off
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .deserializeJsonWith(new GsonDeserializer<>(gson))
            .build();
        // @formatter:on
    }

    public int getEntityFromToken(String token) {
        JwtParser jwtParser = createJwtParser();
        Jws<Claims> jwsClaims = jwtParser.parseClaimsJws(token);
        Claims claims = jwsClaims.getBody();
        return Integer.parseInt(claims.getSubject());
    }

    public Authentication getAuthentication(String token) {
        int pkEntity = getEntityFromToken(token);
        UserDetails userDetails = userDetailsService.loadByEntity(pkEntity);

        return new UsernamePasswordAuthenticationToken(userDetails, "",
                Arrays.asList(new SimpleGrantedAuthority(Entity.ROLE_USER)));
    }

    public String createSessionToken(Entity entity) {
        return createToken(entity, TokenType.SESSION, Optional.empty(), Optional.empty(),
                Optional.empty());
    }

    public String createConfirmToken(Entity entity) {
        return createToken(entity, TokenType.CONFIRM, Optional.empty(), Optional.empty(),
                Optional.empty());
    }

    public String createResetPasswordToken(Entity entity) {
        return createToken(entity, TokenType.RESET_PASSWORD, Optional.of(3_600_000L),
                Optional.empty(), Optional.empty());
    }

    public String createInviteToken(Entity inviter, Entity entity) {
        return createToken(entity, TokenType.INVITE, Optional.of(259_200_000L),
                Optional.of(inviter.getEntity()),
                Optional.of(Map.of("emailAddress", entity.getEmailAddress())));
    }

    private String createToken(Entity entity, TokenType tokenType, Optional<Long> expLength,
            Optional<Integer> issuerEntity, Optional<Map<String, Object>> payload) {
        Date iat = new Date();
        Date exp = new Date(iat.getTime() + expLength.orElse(expireLength));

        // @formatter:off
        Claims claims = Jwts.claims()
            .setSubject(Integer.toString(entity.getEntity()))
            .setIssuer(Integer.toString(issuerEntity.orElse(0)))
            .setIssuedAt(iat)
            .setExpiration(exp)
            .setNotBefore(iat);
        // @formatter:on

        if (payload.isPresent()) {
            claims.putAll(payload.get());
        }

        claims.put("type", tokenType.toString());

        Gson gson = createGson();

        // @formatter:off
        return Jwts.builder()
            .setClaims(claims)
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
            .serializeToJsonWith(new GsonSerializer<>(gson))
            .compact();
        // @formatter:on
    }

    public Optional<String> resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        Optional<Cookie> sessionTokenCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("sessionToken")).findFirst();

        if (sessionTokenCookie.isPresent()) {
            return Optional.of(sessionTokenCookie.get().getValue());
        }

        return Optional.empty();
    }

    public boolean validateSessionToken(String token) throws HttpClientErrorException {
        return validateToken(token, TokenType.SESSION);
    }

    public boolean validateConfirmToken(String token) throws HttpClientErrorException {
        return validateToken(token, TokenType.CONFIRM);
    }

    public boolean validateResetPasswordToken(String token) throws HttpClientErrorException {
        return validateToken(token, TokenType.RESET_PASSWORD);
    }

    public boolean validateInviteToken(String token) throws HttpClientErrorException {
        return validateToken(token, TokenType.INVITE);
    }

    public boolean validateToken(String token, TokenType tokenType)
            throws HttpClientErrorException {
        try {
            Claims claims = extractClaims(token);

            if (!claims.get("type").equals(tokenType.toString())) {
                throw new JwtException("Token is an incorrect type.");
            }
        } catch (JwtException | IllegalArgumentException ex) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                    "Token is invalid or expired");
        }

        return true;
    }

    public Claims extractClaims(String token) {
        JwtParser jwtParser = createJwtParser();
        Jws<Claims> jws = jwtParser.parseClaimsJws(token);
        return jws.getBody();
    }
}
