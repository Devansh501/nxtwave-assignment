package com.nxtwave.security;

import com.nxtwave.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtilService jwtUtilService;

    public void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String authHeader = req.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            chain.doFilter(req,res);
            return;
        }

        String token = authHeader.substring(7);
        String username = null;
        try{
            username = jwtUtilService.getUsernameFromToken(token);
        }
        catch (Exception e){
            chain.doFilter(req,res);
            return;
        }
        if(jwtUtilService.isTokenExpired(token) || SecurityContextHolder.getContext().getAuthentication() != null){
            chain.doFilter(req,res);
            return;
        }

        String role = jwtUtilService.getRoleFromToken(token);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,null, Collections.singletonList(authority));
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        chain.doFilter(req,res);
    }
}
