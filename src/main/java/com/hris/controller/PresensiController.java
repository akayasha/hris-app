package com.hris.controller;

import com.hris.dto.request.SubmitAbsensiRequest;
import com.hris.dto.response.ApiResponse;
import com.hris.service.PresensiService;
import com.hris.util.RoleChecker;
import jakarta.validation.Valid;
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
    public ResponseEntity<ApiResponse<?>> comboStatusAbsen(
            @RequestParam(required = false) Long tglAwal,
            @RequestParam(required = false) Long tglAkhir) {
        return ResponseEntity.ok(ApiResponse.success("Data status absen berhasil diambil.", presensiService.comboStatusAbsen()));
    }

    @GetMapping("/daftar/admin")
    public ResponseEntity<ApiResponse<?>> daftarAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long tglAwal,
            @RequestParam Long tglAkhir) {
        roleChecker.getAdminOrHrd(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(
                "Daftar presensi admin berhasil diambil.",
                presensiService.daftarPresensiAdmin(tglAwal, tglAkhir)
        ));
    }

    @GetMapping("/daftar/pegawai")
    public ResponseEntity<ApiResponse<?>> daftarPegawai(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long tglAwal,
            @RequestParam Long tglAkhir) {
        return ResponseEntity.ok(ApiResponse.success(
                "Daftar presensi pegawai berhasil diambil.",
                presensiService.daftarPresensiPegawai(userDetails.getUsername(), tglAwal, tglAkhir)
        ));
    }

    @GetMapping("/in")
    public ResponseEntity<ApiResponse<?>> checkIn(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Check-in berhasil.", presensiService.checkIn(userDetails.getUsername())));
    }

    @GetMapping("/out")
    public ResponseEntity<ApiResponse<?>> checkOut(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Check-out berhasil.", presensiService.checkOut(userDetails.getUsername())));
    }

    @PostMapping("/abseni")
    public ResponseEntity<ApiResponse<?>> submitAbsensi(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SubmitAbsensiRequest request) {
        presensiService.submitAbsensi(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Keterangan absensi berhasil dicatat."));
    }

}
