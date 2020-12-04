package com.cari.web.server.config;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import com.cari.web.server.domain.Entity;
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

    public Authentication getAuthentication(String token) {
        JwtParser jwtParser = createJwtParser();
        Jws<Claims> jwsClaims = jwtParser.parseClaimsJws(token);

        Claims claims = jwsClaims.getBody();
        int pkEntity = Integer.parseInt(claims.getSubject());

        UserDetails userDetails = userDetailsService.loadByEntity(pkEntity);

        return new UsernamePasswordAuthenticationToken(userDetails, "",
                Arrays.asList(new SimpleGrantedAuthority(Entity.ROLE_USER)));
    }

    public String createToken(Entity entity) {
        Claims claims = Jwts.claims().setSubject(Integer.toString(entity.getEntity()));
        Date iat = new Date();
        Date exp = new Date(iat.getTime() + expireLength);

        Gson gson = createGson();

        // @formatter:off
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(iat)
            .setExpiration(exp)
            .setNotBefore(iat)
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
            .serializeToJsonWith(new GsonSerializer<>(gson))
            .compact();
        // @formatter:on
    }

    public String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        return null;
    }

    public boolean validateToken(String token) throws HttpClientErrorException {
        try {
            JwtParser jwtParser = createJwtParser();
            jwtParser.parseClaimsJws(token);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,
                    "Token is invalid or expired");
        }

        return true;
    }
}
