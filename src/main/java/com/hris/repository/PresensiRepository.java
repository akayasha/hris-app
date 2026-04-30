package com.hris.repository;

import com.hris.entity.Presensi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresensiRepository extends JpaRepository<Presensi, Long> {

    Optional<Presensi> findByPenggunaIdUserAndTglAbsensi(String idUser, Long tglAbsensi);

    List<Presensi> findByTglAbsensiBetweenOrderByTglAbsensiAsc(Long tglAwal, Long tglAkhir);

    List<Presensi> findByPenggunaIdUserAndTglAbsensiBetweenOrderByTglAbsensiAsc(
            String idUser, Long tglAwal, Long tglAkhir);
}
