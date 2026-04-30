package com.hris.service;

import com.hris.dto.response.PenggunaDto;
import com.hris.entity.*;
import com.hris.exception.ApiException;
import com.hris.repository.*;
import com.hris.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PenggunaRepository penggunaRepository;
    private final JabatanRepository jabatanRepository;
    private final DepartemenRepository departemenRepository;
    private final UnitKerjaRepository unitKerjaRepository;
    private final JenisKelaminRepository jenisKelaminRepository;
    private final PendidikanRepository pendidikanRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public Map<String, String> initData(String namaAdmin, String perusahaan) {
        // Cek apakah sudah ada admin
        boolean adminExists = penggunaRepository.findAll().stream()
                .anyMatch(p -> "ADMIN".equals(p.getProfile()));
        if (adminExists) {
            throw new ApiException("Data awal sudah pernah dibuat sebelumnya. Tidak bisa membuat ulang.");
        }

        String email = generateEmail(namaAdmin, perusahaan);
        String rawPassword = generatePassword();

        // Ambil default master data (index pertama)
        Jabatan jabatan = jabatanRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ApiException("Data jabatan belum tersedia. Hubungi administrator sistem."));
        Departemen departemen = departemenRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ApiException("Data departemen belum tersedia."));
        UnitKerja unitKerja = unitKerjaRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ApiException("Data unit kerja belum tersedia."));
        JenisKelamin jenisKelamin = jenisKelaminRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ApiException("Data jenis kelamin belum tersedia."));
        Pendidikan pendidikan = pendidikanRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ApiException("Data pendidikan belum tersedia."));

        Pengguna admin = Pengguna.builder()
                .namaLengkap(namaAdmin)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .profile("ADMIN")
                .nikUser(generateNik())
                .tempatLahir("-")
                .tanggalLahir(0L)
                .jabatan(jabatan)
                .departemen(departemen)
                .unitKerja(unitKerja)
                .jenisKelamin(jenisKelamin)
                .pendidikan(pendidikan)
                .build();

        penggunaRepository.save(admin);

        Map<String, String> result = new LinkedHashMap<>();
        result.put("email", email);
        result.put("password", rawPassword);
        result.put("profile", "ADMIN");
        return result;
    }

    public Map<String, Object> login(String email, String password, String profile) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new ApiException("Email atau password yang Anda masukkan salah. Silakan coba lagi.");
        }

        Pengguna pengguna = penggunaRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Akun dengan email tersebut tidak ditemukan."));

        if (!pengguna.getProfile().equalsIgnoreCase(profile)) {
            throw new ApiException("Profil login tidak sesuai. Silakan pilih profil yang benar.");
        }

        String token = jwtUtil.generateToken(email);

        Map<String, Object> info = new LinkedHashMap<>();
        PenggunaDto dto = PenggunaDto.from(pengguna);
        info.put("profile", dto.getProfile());
        info.put("idUser", dto.getIdUser());
        info.put("namaLengkap", dto.getNamaLengkap());
        info.put("tempatLahir", dto.getTempatLahir());
        info.put("tanggalLahir", dto.getTanggalLahir());
        info.put("email", dto.getEmail());
        info.put("password", dto.getPassword());
        info.put("nikUser", dto.getNikUser());
        info.put("kdJabatan", dto.getKdJabatan());
        info.put("namaJabatan", dto.getNamaJabatan());
        info.put("kdDepartemen", dto.getKdDepartemen());
        info.put("namaDepartemen", dto.getNamaDepartemen());
        info.put("kdUnitKerja", dto.getKdUnitKerja());
        info.put("namaUnitKerja", dto.getNamaUnitKerja());
        info.put("kdJenisKelamin", dto.getKdJenisKelamin());
        info.put("namaJenisKelamin", dto.getNamaJenisKelamin());
        info.put("kdPendidikan", dto.getKdPendidikan());
        info.put("namaPendidikan", dto.getNamaPendidikan());
        info.put("photo", dto.getPhoto());

        Map<String, Object> hasil = new LinkedHashMap<>();
        hasil.put("token", token);
        hasil.put("info", info);

        return Map.of("hasil", hasil);
    }

    public void ubahPasswordSendiri(String emailPengguna, String passwordAsli,
                                     String passwordBaru1, String passwordBaru2) {
        Pengguna pengguna = penggunaRepository.findByEmail(emailPengguna)
                .orElseThrow(() -> new ApiException("Akun tidak ditemukan."));

        if (!passwordEncoder.matches(passwordAsli, pengguna.getPassword())) {
            throw new ApiException("Password lama yang Anda masukkan tidak tepat.");
        }
        if (!passwordBaru1.equals(passwordBaru2)) {
            throw new ApiException("Password baru dan konfirmasi password tidak cocok.");
        }
        if (passwordBaru1.length() < 6) {
            throw new ApiException("Password baru minimal harus 6 karakter.");
        }

        pengguna.setPassword(passwordEncoder.encode(passwordBaru1));
        penggunaRepository.save(pengguna);
    }

    // --- Helper ---

    private String generateEmail(String nama, String perusahaan) {
        String namaPart = nama.toLowerCase().replaceAll("\\s+", ".");
        String perusahaanPart = perusahaan.toLowerCase().replaceAll("\\s+", "");
        return namaPart + "@" + perusahaanPart + ".com";
    }

    private String generatePassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }

    private String generateNik() {
        return String.valueOf(System.currentTimeMillis()).substring(3);
    }
}
