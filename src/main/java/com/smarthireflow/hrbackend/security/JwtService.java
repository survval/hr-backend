package com.smarthireflow.hrbackend.security;

import com.smarthireflow.hrbackend.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
  private final Key key;
  private final String issuer;
  private final long expirationMinutes;

  public JwtService(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.issuer}") String issuer,
                    @Value("${jwt.expiration-minutes}") long expirationMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.issuer = issuer;
    this.expirationMinutes = expirationMinutes;
  }

  public String generate(String subject, Role role) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(subject)
        .issuer(issuer)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
        .claims(Map.of("role", role.name()))
        .signWith(key)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
  }
}