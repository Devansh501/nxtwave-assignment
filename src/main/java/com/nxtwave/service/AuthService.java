package com.nxtwave.service;

import com.nxtwave.dto.AuthDtos;
import com.nxtwave.entity.User;
import com.nxtwave.repository.UserRepository;
import com.nxtwave.security.JwtUtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtilService jwtUtilService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        return AuthDtos.AuthResponse.builder()
                .accessToken(jwtUtilService.generateAccessToken(user))
                .refreshToken(jwtUtilService.generateRefreshToken(user))
                .build();
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return AuthDtos.AuthResponse.builder()
                .accessToken(jwtUtilService.generateAccessToken(user))
                .refreshToken(jwtUtilService.generateRefreshToken(user))
                .build();
    }

    public AuthDtos.AuthResponse refreshToken(AuthDtos.RefreshTokenRequest request) {
        String token = request.getRefreshToken();

        String username = jwtUtilService.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!jwtUtilService.isTokenExpired(token)) {
            return AuthDtos.AuthResponse.builder()
                    .accessToken(jwtUtilService.generateAccessToken(user))
                    .refreshToken(token)
                    .build();
        }

        throw new RuntimeException("Refresh token is invalid or expired");
    }
}