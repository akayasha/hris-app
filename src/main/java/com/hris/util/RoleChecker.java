package com.hris.util;

import com.hris.entity.Pengguna;
import com.hris.exception.ApiException;
import com.hris.repository.PenggunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleChecker {

    private final PenggunaRepository penggunaRepository;

    public Pengguna getAdminOrHrd(String email) {
        Pengguna p = penggunaRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("Akun tidak ditemukan."));

        boolean isAdmin = "ADMIN".equalsIgnoreCase(p.getProfile());
        boolean isHrd = p.getDepartemen() != null &&
                "HRD".equalsIgnoreCase(p.getDepartemen().getNamaDepartemen());

        if (!isAdmin && !isHrd) {
            throw new ApiException("Anda tidak memiliki akses untuk fitur ini. Hanya Admin atau pegawai HRD yang diizinkan.");
        }
        return p;
    }
}
