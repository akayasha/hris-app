package com.hris.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jenis_kelamin")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JenisKelamin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kd_jenis_kelamin")
    private Integer kdJenisKelamin;

    @Column(name = "nama_jenis_kelamin", nullable = false, unique = true)
    private String namaJenisKelamin;
}
