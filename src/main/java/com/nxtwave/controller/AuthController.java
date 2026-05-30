package com.nxtwave.controller;


import com.nxtwave.dto.ApiResponse;
import com.nxtwave.dto.AuthDtos;
import com.nxtwave.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> register(@RequestBody AuthDtos.RegisterRequest request) {
        ApiResponse<AuthDtos.AuthResponse> response = ApiResponse.<AuthDtos.AuthResponse>builder()
                .status("success")
                .message("Register successful")
                .data(authService.register(request))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> login(@RequestBody AuthDtos.LoginRequest request) {
        ApiResponse<AuthDtos.AuthResponse> response = ApiResponse.<AuthDtos.AuthResponse>builder()
                .status("success")
                .message("Login successful")
                .data(authService.login(request))
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDtos.AuthResponse>> refreshToken(@RequestBody AuthDtos.RefreshTokenRequest request) {
        ApiResponse<AuthDtos.AuthResponse> response = ApiResponse.<AuthDtos.AuthResponse>builder()
                .status("success")
                .message("Refresh successful")
                .data(authService.refreshToken(request))
                .build();
        return ResponseEntity.ok(response);
    }
}