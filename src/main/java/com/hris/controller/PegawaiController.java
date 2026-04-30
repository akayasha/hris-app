package com.hris.controller;

import com.hris.dto.request.CreatePegawaiRequest;
import com.hris.dto.request.UpdatePegawaiRequest;
import com.hris.dto.response.ApiResponse;
import com.hris.service.PegawaiService;
import com.hris.util.RoleChecker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PegawaiController {

    private final PegawaiService pegawaiService;
    private final RoleChecker roleChecker;

    // ========== COMBO ==========

    @GetMapping("/api/pegawai/combo/jabatan")
    public ResponseEntity<ApiResponse<?>> comboJabatan() {
        return ResponseEntity.ok(ApiResponse.success("Data jabatan berhasil diambil.", pegawaiService.comboJabatan()));
    }

    @GetMapping("/api/pegawai/combo/departemen")
    public ResponseEntity<ApiResponse<?>> comboDepartemen() {
        return ResponseEntity.ok(ApiResponse.success("Data departemen berhasil diambil.", pegawaiService.comboDepartemen()));
    }

    @GetMapping("/api/pegawai/combo/unit-kerja")
    public ResponseEntity<ApiResponse<?>> comboUnitKerja() {
        return ResponseEntity.ok(ApiResponse.success("Data unit kerja berhasil diambil.", pegawaiService.comboUnitKerja()));
    }

    @GetMapping("/api/pegawai/combo/pendidikan")
    public ResponseEntity<ApiResponse<?>> comboPendidikan() {
        return ResponseEntity.ok(ApiResponse.success("Data pendidikan berhasil diambil.", pegawaiService.comboPendidikan()));
    }

    @GetMapping("/api/pegawai/combo/jenis-kelamin")
    public ResponseEntity<ApiResponse<?>> comboJenisKelamin() {
        return ResponseEntity.ok(ApiResponse.success("Data jenis kelamin berhasil diambil.", pegawaiService.comboJenisKelamin()));
    }

    @GetMapping("/api/pegawai/combo/departemen-hrd")
    public ResponseEntity<ApiResponse<?>> comboDepartemenHrd() {
        return ResponseEntity.ok(ApiResponse.success("Data departemen HRD berhasil diambil.", pegawaiService.comboDepartemenHrd()));
    }

    // ========== DAFTAR ==========

    @GetMapping("/api/pegawai/daftar")
    public ResponseEntity<ApiResponse<?>> daftarPegawai(@AuthenticationPrincipal UserDetails userDetails) {
        roleChecker.getAdminOrHrd(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Daftar pegawai berhasil diambil.", pegawaiService.daftarPegawai()));
    }

    // ========== TAMBAH ==========

    @PostMapping("/pegawai/admin-tambah-pegawai")
    public ResponseEntity<ApiResponse<?>> tambahPegawai(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePegawaiRequest request) {
        roleChecker.getAdminOrHrd(userDetails.getUsername());
        pegawaiService.tambahPegawai(request);
        return ResponseEntity.ok(ApiResponse.success("Pegawai berhasil ditambahkan."));
    }

    // ========== UBAH ==========

    @PostMapping("/pegawai/admin-ubah-pegawai")
    public ResponseEntity<ApiResponse<?>> ubahPegawai(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdatePegawaiRequest request) {
        roleChecker.getAdminOrHrd(userDetails.getUsername());
        pegawaiService.ubahPegawai(request);
        return ResponseEntity.ok(ApiResponse.success("Data pegawai berhasil diubah."));
    }

    // ========== PHOTO ==========

    @PostMapping("/pegawai/admin-ubah-photo")
    public ResponseEntity<ApiResponse<?>> ubahPhotoAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String idUser,
            @RequestParam String namaFile,
            @RequestParam("files") MultipartFile file) {

        roleChecker.getAdminOrHrd(userDetails.getUsername());
        String path = pegawaiService.ubahPhotoAdmin(idUser, namaFile, file);
        return ResponseEntity.ok(ApiResponse.success("Foto pegawai berhasil diubah.", Map.of("photo", path)));
    }

    @PostMapping("/pegawai/ubah-photo")
    public ResponseEntity<ApiResponse<?>> ubahPhotoSendiri(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String namaFile,
            @RequestParam("files") MultipartFile file) {

        String path = pegawaiService.ubahPhotoSendiri(userDetails.getUsername(), namaFile, file);
        return ResponseEntity.ok(ApiResponse.success("Foto profil berhasil diubah.", Map.of("photo", path)));
    }
}
