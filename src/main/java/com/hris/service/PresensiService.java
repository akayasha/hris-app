package com.hris.service;

import com.hris.dto.request.SubmitAbsensiRequest;
import com.hris.entity.*;
import com.hris.exception.ApiException;
import com.hris.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresensiService {

    private final PresensiRepository presensiRepository;
    private final PenggunaRepository penggunaRepository;
    private final StatusAbsenRepository statusAbsenRepository;

    private static final ZoneId ZONE_WIB = ZoneId.of("Asia/Jakarta");
    private static final DateTimeFormatter JAM_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ---------- COMBO ----------

    public List<Map<String, Object>> comboStatusAbsen() {
        return statusAbsenRepository.findAll().stream()
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("kdStatus", s.getKdStatus());
                    m.put("namaStatus", s.getNamaStatus());
                    return m;
                }).collect(Collectors.toList());
    }

    // ---------- DAFTAR ADMIN ----------

    public List<Map<String, Object>> daftarPresensiAdmin(Long tglAwal, Long tglAkhir) {
        return presensiRepository.findByTglAbsensiBetweenOrderByTglAbsensiAsc(tglAwal, tglAkhir)
                .stream().map(pr -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idUser", pr.getPengguna().getIdUser());
                    m.put("namaLengkap", pr.getPengguna().getNamaLengkap());
                    m.put("tglAbsensi", pr.getTglAbsensi());
                    m.put("jamMasuk", pr.getJamMasuk());
                    m.put("jamKeluar", pr.getJamKeluar());
                    m.put("namaStatus", pr.getStatusAbsen() != null ? pr.getStatusAbsen().getNamaStatus() : null);
                    return m;
                }).collect(Collectors.toList());
    }

    // ---------- DAFTAR PEGAWAI ----------

    public List<Map<String, Object>> daftarPresensiPegawai(String email, Long tglAwal, Long tglAkhir) {
        Pengguna pengguna = getPengguna(email);
        String idUser = pengguna.getIdUser();
        return presensiRepository
                .findByPenggunaIdUserAndTglAbsensiBetweenOrderByTglAbsensiAsc(idUser, tglAwal, tglAkhir)
                .stream().map(pr -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("tglAbsensi", pr.getTglAbsensi());
                    m.put("jamMasuk", pr.getJamMasuk());
                    m.put("jamKeluar", pr.getJamKeluar());
                    m.put("namaStatus", pr.getStatusAbsen() != null ? pr.getStatusAbsen().getNamaStatus() : null);
                    return m;
                }).collect(Collectors.toList());
    }

    // ---------- CHECK-IN ----------

    public Map<String, String> checkIn(String emailPengguna) {
        Pengguna pengguna = getPengguna(emailPengguna);
        Long tglHariIni = getEpochHariIni();

        Optional<Presensi> existing = presensiRepository
                .findByPenggunaIdUserAndTglAbsensi(pengguna.getIdUser(), tglHariIni);

        if (existing.isPresent() && existing.get().getJamMasuk() != null) {
            throw new ApiException("Anda sudah melakukan check-in hari ini pada pukul " + existing.get().getJamMasuk() + ".");
        }

        StatusAbsen statusHadir = statusAbsenRepository.findAll().stream()
                .filter(s -> s.getNamaStatus().equalsIgnoreCase("Hadir"))
                .findFirst()
                .orElseThrow(() -> new ApiException("Status 'Hadir' tidak ditemukan. Hubungi administrator."));

        String jamMasuk = LocalTime.now(ZONE_WIB).format(JAM_FORMAT);

        Presensi presensi = existing.orElse(Presensi.builder()
                .pengguna(pengguna)
                .tglAbsensi(tglHariIni)
                .build());

        presensi.setJamMasuk(jamMasuk);
        presensi.setStatusAbsen(statusHadir);
        presensiRepository.save(presensi);

        return Map.of("jamMasuk", jamMasuk);
    }

    // ---------- CHECK-OUT ----------

    public Map<String, String> checkOut(String emailPengguna) {
        Pengguna pengguna = getPengguna(emailPengguna);
        Long tglHariIni = getEpochHariIni();

        Presensi presensi = presensiRepository
                .findByPenggunaIdUserAndTglAbsensi(pengguna.getIdUser(), tglHariIni)
                .orElseThrow(() -> new ApiException("Anda belum melakukan check-in hari ini. Silakan check-in terlebih dahulu."));

        if (presensi.getJamMasuk() == null) {
            throw new ApiException("Data check-in hari ini tidak valid. Silakan hubungi HRD.");
        }
        if (presensi.getJamKeluar() != null) {
            throw new ApiException("Anda sudah melakukan check-out hari ini pada pukul " + presensi.getJamKeluar() + ".");
        }

        String jamKeluar = LocalTime.now(ZONE_WIB).format(JAM_FORMAT);
        presensi.setJamKeluar(jamKeluar);
        presensiRepository.save(presensi);

        return Map.of("jamKeluar", jamKeluar);
    }

    // ---------- ABSENSI (Tidak Masuk) ----------

    public void submitAbsensi(String emailPengguna, SubmitAbsensiRequest request) {
        Pengguna pengguna = getPengguna(emailPengguna);

        StatusAbsen status = statusAbsenRepository.findById(request.kdStatus())
                .orElseThrow(() -> new ApiException("Status absen dengan kode " + request.kdStatus() + " tidak ditemukan."));

        if (status.getNamaStatus().equalsIgnoreCase("Hadir")) {
            throw new ApiException("Untuk status 'Hadir' silakan gunakan fitur check-in, bukan form absensi.");
        }

        Optional<Presensi> existing = presensiRepository
                .findByPenggunaIdUserAndTglAbsensi(pengguna.getIdUser(), request.tglAbsensi());

        if (existing.isPresent()) {
            Presensi pr = existing.get();
            if (pr.getJamMasuk() != null) {
                throw new ApiException("Anda sudah tercatat hadir pada tanggal tersebut. Tidak bisa mengajukan absensi.");
            }
            pr.setStatusAbsen(status);
            presensiRepository.save(pr);
        } else {
            Presensi pr = Presensi.builder()
                    .pengguna(pengguna)
                    .tglAbsensi(request.tglAbsensi())
                    .statusAbsen(status)
                    .build();
            presensiRepository.save(pr);
        }
    }

    // ---------- HELPERS ----------

    private Pengguna getPengguna(String email) {
        return penggunaRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Akun tidak ditemukan."));
    }

    private Long getEpochHariIni() {
        LocalDate today = LocalDate.now(ZONE_WIB);
        return today.atStartOfDay(ZONE_WIB).toEpochSecond();
    }
}
