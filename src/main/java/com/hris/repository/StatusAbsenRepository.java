package com.hris.repository;
import com.hris.entity.StatusAbsen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface StatusAbsenRepository extends JpaRepository<StatusAbsen, Integer> {}
