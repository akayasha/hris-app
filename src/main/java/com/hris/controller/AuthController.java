package com.hris.controller;

import com.hris.service.AuthService;
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
    public ResponseEntity<?> initData(
            @RequestParam String namaAdmin,
            @RequestParam String perusahaan) {
        return ResponseEntity.ok(authService.initData(namaAdmin, perusahaan));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String profile) {
        return ResponseEntity.ok(authService.login(email, password, profile));
    }

    @PostMapping("/ubah-password-sendiri")
    public ResponseEntity<?> ubahPasswordSendiri(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String passwordAsli,
            @RequestParam String passwordBaru1,
            @RequestParam String passwordBaru2) {
        authService.ubahPasswordSendiri(userDetails.getUsername(), passwordAsli, passwordBaru1, passwordBaru2);
        return ResponseEntity.ok(Map.of("pesan", "Password berhasil diubah."));
    }
}
