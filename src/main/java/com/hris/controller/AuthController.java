package com.hris.controller;

import com.hris.dto.request.ChangePasswordRequest;
import com.hris.dto.request.InitDataRequest;
import com.hris.dto.request.LoginRequest;
import com.hris.dto.response.ApiResponse;
import com.hris.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/init-data")
    public ResponseEntity<ApiResponse<?>> initData(@Valid @RequestBody InitDataRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Data awal berhasil dibuat.",
                authService.initData(request)
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Login berhasil.",
                authService.login(request)
        ));
    }

    @PostMapping("/ubah-password-sendiri")
    public ResponseEntity<ApiResponse<?>> ubahPasswordSendiri(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.ubahPasswordSendiri(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Password berhasil diubah."));
    }
}
