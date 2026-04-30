package com.hris.controller;

import com.hris.service.PresensiService;
import com.hris.util.RoleChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/presensi")
@RequiredArgsConstructor
public class PresensiController {

    private final PresensiService presensiService;
    private final RoleChecker roleChecker;

    @GetMapping("/combo/status-absen")
    public ResponseEntity<?> comboStatusAbsen(
            @RequestParam(required = false) Long tglAwal,
            @RequestParam(required = false) Long tglAkhir) {
        return ResponseEntity.ok(presensiService.comboStatusAbsen());
    }

    @GetMapping("/daftar/admin")
    public ResponseEntity<?> daftarAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long tglAwal,
            @RequestParam Long tglAkhir) {
        roleChecker.getAdminOrHrd(userDetails.getUsername());
        return ResponseEntity.ok(presensiService.daftarPresensiAdmin(tglAwal, tglAkhir));
    }

    @GetMapping("/daftar/pegawai")
    public ResponseEntity<?> daftarPegawai(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long tglAwal,
            @RequestParam Long tglAkhir) {
        return ResponseEntity.ok(presensiService.daftarPresensiPegawai(userDetails.getUsername(), tglAwal, tglAkhir));
    }

    @GetMapping("/in")
    public ResponseEntity<?> checkIn(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(presensiService.checkIn(userDetails.getUsername()));
    }

    @GetMapping("/out")
    public ResponseEntity<?> checkOut(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(presensiService.checkOut(userDetails.getUsername()));
    }

    @PostMapping("/abseni")
    public ResponseEntity<?> submitAbsensi(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long tglAbsensi,
            @RequestParam Integer kdStatus) {
        presensiService.submitAbsensi(userDetails.getUsername(), tglAbsensi, kdStatus);
        return ResponseEntity.ok(Map.of("pesan", "Keterangan absensi berhasil dicatat."));
    }

}

