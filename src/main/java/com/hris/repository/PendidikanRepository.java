package com.hris.repository;
import com.hris.entity.Pendidikan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface PendidikanRepository extends JpaRepository<Pendidikan, Integer> {}
