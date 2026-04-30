package com.hris.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pendidikan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pendidikan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kd_pendidikan")
    private Integer kdPendidikan;

    @Column(name = "nama_pendidikan", nullable = false, unique = true)
    private String namaPendidikan;
}
