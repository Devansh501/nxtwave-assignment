package com.nxtwave.security;

import com.nxtwave.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtUtilService {
    @Value("${auth.jwt.secret}")
    private String jwtSecret;

    @Value("${auth.jwt.access.expiry}")
    private long authAccessExpiration;

    @Value("${auth.jwt.refresh.expiry}")
    private long authRefreshExpiration;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(User user,long expiry){
        return Jwts.builder().setSubject(user.getUsername()).claim("role", user.getRole().name()).
                setExpiration(new Date(System.currentTimeMillis() + expiry)).setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSigningKey()).compact();
    }

    public String generateAccessToken(User user){
        return generateToken(user,authAccessExpiration);
    }

    public String generateRefreshToken(User user){
        return generateToken(user,authRefreshExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver){
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, claims -> claims.getSubject());
    }

    public boolean isTokenExpired(String token){
        return getClaimFromToken(token, claims -> claims.getExpiration()).before(new Date());
    }


    public String getRoleFromToken(String token){
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }
}
