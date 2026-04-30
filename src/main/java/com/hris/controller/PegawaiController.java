package com.hris.controller;

import com.hris.service.PegawaiService;
import com.hris.util.RoleChecker;
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
    public ResponseEntity<?> comboJabatan() {
        return ResponseEntity.ok(pegawaiService.comboJabatan());
    }

    @GetMapping("/api/pegawai/combo/departemen")
    public ResponseEntity<?> comboDepartemen() {
        return ResponseEntity.ok(pegawaiService.comboDepartemen());
    }

    @GetMapping("/api/pegawai/combo/unit-kerja")
    public ResponseEntity<?> comboUnitKerja() {
        return ResponseEntity.ok(pegawaiService.comboUnitKerja());
    }

    @GetMapping("/api/pegawai/combo/pendidikan")
    public ResponseEntity<?> comboPendidikan() {
        return ResponseEntity.ok(pegawaiService.comboPendidikan());
    }

    @GetMapping("/api/pegawai/combo/jenis-kelamin")
    public ResponseEntity<?> comboJenisKelamin() {
        return ResponseEntity.ok(pegawaiService.comboJenisKelamin());
    }

    @GetMapping("/api/pegawai/combo/departemen-hrd")
    public ResponseEntity<?> comboDepartemenHrd() {
        return ResponseEntity.ok(pegawaiService.comboDepartemenHrd());
    }

    // ========== DAFTAR ==========

    @GetMapping("/api/pegawai/daftar")
    public ResponseEntity<?> daftarPegawai(@AuthenticationPrincipal UserDetails userDetails) {
        roleChecker.getAdminOrHrd(userDetails.getUsername());
        return ResponseEntity.ok(pegawaiService.daftarPegawai());
    }

    // ========== TAMBAH ==========

    @PostMapping("/pegawai/admin-tambah-pegawai")
    public ResponseEntity<?> tambahPegawai(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String namaLengkap,
            @RequestParam String email,
            @RequestParam String tempatLahir,
            @RequestParam Long tanggalLahir,
            @RequestParam Integer kdJenisKelamin,
            @RequestParam Integer kdPendidikan,
            @RequestParam Integer kdJabatan,
            @RequestParam Integer kdDepartemen,
            @RequestParam Integer kdUnitKerja,
            @RequestParam String password,
            @RequestParam String passwordC) {

        roleChecker.getAdminOrHrd(userDetails.getUsername());
        pegawaiService.tambahPegawai(namaLengkap, email, tempatLahir, tanggalLahir,
                kdJenisKelamin, kdPendidikan, kdJabatan, kdDepartemen, kdUnitKerja,
                password, passwordC);
        return ResponseEntity.ok(Map.of("pesan", "Pegawai berhasil ditambahkan."));
    }

    // ========== UBAH ==========

    @PostMapping("/pegawai/admin-ubah-pegawai")
    public ResponseEntity<?> ubahPegawai(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String idUser,
            @RequestParam String namaLengkap,
            @RequestParam String email,
            @RequestParam String tempatLahir,
            @RequestParam Long tanggalLahir,
            @RequestParam Integer kdJenisKelamin,
            @RequestParam Integer kdPendidikan,
            @RequestParam Integer kdJabatan,
            @RequestParam Integer kdDepartemen,
            @RequestParam Integer kdUnitKerja,
            @RequestParam String password,
            @RequestParam String passwordC) {

        roleChecker.getAdminOrHrd(userDetails.getUsername());
        pegawaiService.ubahPegawai(idUser, namaLengkap, email, tempatLahir, tanggalLahir,
                kdJenisKelamin, kdPendidikan, kdJabatan, kdDepartemen, kdUnitKerja,
                password, passwordC);
        return ResponseEntity.ok(Map.of("pesan", "Data pegawai berhasil diubah."));
    }

    // ========== PHOTO ==========

    @PostMapping("/pegawai/admin-ubah-photo")
    public ResponseEntity<?> ubahPhotoAdmin(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String idUser,
            @RequestParam String namaFile,
            @RequestParam("files") MultipartFile file) {

        roleChecker.getAdminOrHrd(userDetails.getUsername());
        String path = pegawaiService.ubahPhotoAdmin(idUser, namaFile, file);
        return ResponseEntity.ok(Map.of("photo", path));
    }

    @PostMapping("/pegawai/ubah-photo")
    public ResponseEntity<?> ubahPhotoSendiri(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String namaFile,
            @RequestParam("files") MultipartFile file) {

        String path = pegawaiService.ubahPhotoSendiri(userDetails.getUsername(), namaFile, file);
        return ResponseEntity.ok(Map.of("photo", path));
    }
}
