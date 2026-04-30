package com.hris.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "jabatan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jabatan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kd_jabatan")
    private Integer kdJabatan;

    @Column(name = "nama_jabatan", nullable = false, unique = true)
    private String namaJabatan;
}
