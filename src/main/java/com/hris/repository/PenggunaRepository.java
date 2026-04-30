package com.hris.repository;

import com.hris.entity.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, String> {

    Optional<Pengguna> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT p FROM Pengguna p WHERE p.departemen.namaDepartemen = 'HRD'")
    List<Pengguna> findByDepartemenHrd();
}
