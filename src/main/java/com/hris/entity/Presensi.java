package com.hris.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "presensi",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_user", "tgl_absensi"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Presensi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private Pengguna pengguna;

    // epoch seconds (date only, no time)
    @Column(name = "tgl_absensi", nullable = false)
    private Long tglAbsensi;

    @Column(name = "jam_masuk")
    private String jamMasuk;

    @Column(name = "jam_keluar")
    private String jamKeluar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kd_status")
    private StatusAbsen statusAbsen;
}
