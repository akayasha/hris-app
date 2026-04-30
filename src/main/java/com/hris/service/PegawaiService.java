package com.hris.service;

import com.hris.dto.response.PenggunaDto;
import com.hris.entity.*;
import com.hris.exception.ApiException;
import com.hris.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PegawaiService {

    private final PenggunaRepository penggunaRepository;
    private final JabatanRepository jabatanRepository;
    private final DepartemenRepository departemenRepository;
    private final UnitKerjaRepository unitKerjaRepository;
    private final JenisKelaminRepository jenisKelaminRepository;
    private final PendidikanRepository pendidikanRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir}")
    private String uploadDir;

    // ---------- COMBO ----------

    public List<Map<String, Object>> comboJabatan() {
        return jabatanRepository.findAll().stream()
                .map(j -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("kdJabatan", j.getKdJabatan());
                    m.put("namaJabatan", j.getNamaJabatan());
                    return m;
                }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> comboDepartemen() {
        return departemenRepository.findAll().stream()
                .map(d -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("kdDepartemen", d.getKdDepartemen());
                    m.put("namaDepartemen", d.getNamaDepartemen());
                    return m;
                }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> comboUnitKerja() {
        return unitKerjaRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("kdUnitKerja", u.getKdUnitKerja());
                    m.put("namaUnitKerja", u.getNamaUnitKerja());
                    return m;
                }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> comboPendidikan() {
        return pendidikanRepository.findAll().stream()
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("kdPendidikan", p.getKdPendidikan());
                    m.put("namaPendidkan", p.getNamaPendidikan()); // typo sesuai spek
                    return m;
                }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> comboJenisKelamin() {
        return jenisKelaminRepository.findAll().stream()
                .map(j -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("kdJenisKelamin", j.getKdJenisKelamin());
                    m.put("namaJenisKelamin", j.getNamaJenisKelamin());
                    return m;
                }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> comboDepartemenHrd() {
        return penggunaRepository.findByDepartemenHrd().stream()
                .map(p -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("namaLengkap", p.getNamaLengkap());
                    m.put("kdJabatan", p.getJabatan() != null ? p.getJabatan().getKdJabatan() : null);
                    m.put("namaJabatan", p.getJabatan() != null ? p.getJabatan().getNamaJabatan() : null);
                    return m;
                }).collect(Collectors.toList());
    }

    // ---------- DAFTAR ----------

    public List<PenggunaDto> daftarPegawai() {
        return penggunaRepository.findAll().stream()
                .map(PenggunaDto::from)
                .collect(Collectors.toList());
    }

    // ---------- TAMBAH ----------

    public void tambahPegawai(String namaLengkap, String email, String tempatLahir,
                               Long tanggalLahir, Integer kdJenisKelamin, Integer kdPendidikan,
                               Integer kdJabatan, Integer kdDepartemen, Integer kdUnitKerja,
                               String password, String passwordC) {
        validatePasswordMatch(password, passwordC);
        if (penggunaRepository.existsByEmail(email)) {
            throw new ApiException("Email " + email + " sudah digunakan oleh pegawai lain.");
        }

        Pengguna p = Pengguna.builder()
                .namaLengkap(namaLengkap)
                .email(email)
                .tempatLahir(tempatLahir)
                .tanggalLahir(tanggalLahir)
                .password(passwordEncoder.encode(password))
                .profile("PEGAWAI")
                .nikUser(generateNik())
                .jenisKelamin(findJenisKelamin(kdJenisKelamin))
                .pendidikan(findPendidikan(kdPendidikan))
                .jabatan(findJabatan(kdJabatan))
                .departemen(findDepartemen(kdDepartemen))
                .unitKerja(findUnitKerja(kdUnitKerja))
                .build();

        penggunaRepository.save(p);
    }

    // ---------- UBAH ----------

    public void ubahPegawai(String idUser, String namaLengkap, String email, String tempatLahir,
                             Long tanggalLahir, Integer kdJenisKelamin, Integer kdPendidikan,
                             Integer kdJabatan, Integer kdDepartemen, Integer kdUnitKerja,
                             String password, String passwordC) {
        validatePasswordMatch(password, passwordC);

        Pengguna p = penggunaRepository.findById(idUser)
                .orElseThrow(() -> new ApiException("Pegawai dengan ID tersebut tidak ditemukan."));

        // Cek email tidak dipakai orang lain
        penggunaRepository.findByEmail(email)
                .filter(existing -> !existing.getIdUser().equals(idUser))
                .ifPresent(x -> { throw new ApiException("Email " + email + " sudah digunakan pegawai lain."); });

        p.setNamaLengkap(namaLengkap);
        p.setEmail(email);
        p.setTempatLahir(tempatLahir);
        p.setTanggalLahir(tanggalLahir);
        p.setPassword(passwordEncoder.encode(password));
        p.setJenisKelamin(findJenisKelamin(kdJenisKelamin));
        p.setPendidikan(findPendidikan(kdPendidikan));
        p.setJabatan(findJabatan(kdJabatan));
        p.setDepartemen(findDepartemen(kdDepartemen));
        p.setUnitKerja(findUnitKerja(kdUnitKerja));

        penggunaRepository.save(p);
    }

    // ---------- PHOTO ----------

    public String ubahPhotoAdmin(String idUser, String namaFile, MultipartFile file) {
        Pengguna p = penggunaRepository.findById(idUser)
                .orElseThrow(() -> new ApiException("Pegawai dengan ID tersebut tidak ditemukan."));
        return simpanPhoto(p, namaFile, file);
    }

    public String ubahPhotoSendiri(String emailPengguna, String namaFile, MultipartFile file) {
        Pengguna p = penggunaRepository.findByEmail(emailPengguna)
                .orElseThrow(() -> new ApiException("Akun tidak ditemukan."));
        return simpanPhoto(p, namaFile, file);
    }

    private String simpanPhoto(Pengguna p, String namaFile, MultipartFile file) {
        validateImageFile(file);
        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(dir);

            String ext = getExtension(file.getOriginalFilename());
            String fileName = p.getIdUser() + "_" + System.currentTimeMillis() + "." + ext;
            Path target = dir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            String photoPath = "/uploads/" + fileName;
            p.setPhoto(photoPath);
            penggunaRepository.save(p);
            return photoPath;
        } catch (IOException e) {
            throw new ApiException("Gagal menyimpan foto. Silakan coba lagi.");
        }
    }

    // ---------- HELPERS ----------

    private void validatePasswordMatch(String p1, String p2) {
        if (!p1.equals(p2)) {
            throw new ApiException("Password dan konfirmasi password tidak cocok.");
        }
        if (p1.length() < 6) {
            throw new ApiException("Password minimal harus 6 karakter.");
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException("File foto tidak boleh kosong.");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw new ApiException("File yang diunggah harus berupa gambar (jpg, png, dll).");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private Jabatan findJabatan(Integer kd) {
        return jabatanRepository.findById(kd)
                .orElseThrow(() -> new ApiException("Jabatan dengan kode " + kd + " tidak ditemukan."));
    }

    private Departemen findDepartemen(Integer kd) {
        return departemenRepository.findById(kd)
                .orElseThrow(() -> new ApiException("Departemen dengan kode " + kd + " tidak ditemukan."));
    }

    private UnitKerja findUnitKerja(Integer kd) {
        return unitKerjaRepository.findById(kd)
                .orElseThrow(() -> new ApiException("Unit kerja dengan kode " + kd + " tidak ditemukan."));
    }

    private JenisKelamin findJenisKelamin(Integer kd) {
        return jenisKelaminRepository.findById(kd)
                .orElseThrow(() -> new ApiException("Jenis kelamin dengan kode " + kd + " tidak ditemukan."));
    }

    private Pendidikan findPendidikan(Integer kd) {
        return pendidikanRepository.findById(kd)
                .orElseThrow(() -> new ApiException("Pendidikan dengan kode " + kd + " tidak ditemukan."));
    }

    private String generateNik() {
        return "NIK" + System.currentTimeMillis();
    }
}
