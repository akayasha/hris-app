package com.hris.repository;

import com.hris.entity.Jabatan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JabatanRepository extends JpaRepository<Jabatan, Integer> {}
