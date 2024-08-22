package com.patientpal.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private Key key;

    private final String base64Secret;
    public final long refreshTokenExpirationTime;
    public final long accessTokenExpirationTime;

    private final RedisTemplate<String, Object> redisTemplate;

    public JwtTokenProvider(
            @Value("${security.jwt.base64-secret}") String base64Secret,
            @Value("${security.jwt.refresh-expiration-time}") long refreshTokenExpirationTime,

            @Value("${security.jwt.access-expiration-time}") long accessTokenExpirationTime,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.base64Secret = base64Secret;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.redisTemplate = redisTemplate;
    }

    public String createToken(Authentication authentication, long expirationTime) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        log.info("Creating token for user {}: Authorities - {}", authentication.getName(), authorities);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenExpirationTime);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenExpirationTime);
    }

    private Jws<Claims> getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token)
                .getBody()
                .getSubject();
    }

    @PostConstruct
    public void init() {
        byte[] secretKeyBytes = Decoders.BASE64.decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(secretKeyBytes);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getAllClaimsFromToken(token).getBody();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(Optional.ofNullable(claims.get(AUTHORITIES_KEY))
                        .map(Object::toString)
                        .orElse("")
                        .split(","))
                    .map(String::trim)
                    .filter(auth -> !auth.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            getAllClaimsFromToken(token);
            String isInvalid = (String) redisTemplate.opsForValue().get(token);
            if ("invalid".equals(isInvalid)) {
                return false;
            }
            return true;
        } catch (JwtException ex) {
            log.trace("Invalid JWT token trace: {}", ex.toString());
            return false;
        }
    }

    public void invalidateToken(String token) {
        redisTemplate.opsForValue().set(token, "invalid", 1, TimeUnit.MILLISECONDS);
    }
}
