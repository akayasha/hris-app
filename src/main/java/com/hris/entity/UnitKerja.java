package com.hris.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "unit_kerja")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitKerja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kd_unit_kerja")
    private Integer kdUnitKerja;

    @Column(name = "nama_unit_kerja", nullable = false, unique = true)
    private String namaUnitKerja;
}
