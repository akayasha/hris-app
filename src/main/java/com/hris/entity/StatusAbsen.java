package com.hris.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "status_absen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusAbsen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kd_status")
    private Integer kdStatus;

    @Column(name = "nama_status", nullable = false, unique = true)
    private String namaStatus;
}
