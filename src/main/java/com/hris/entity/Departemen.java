package com.hris.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departemen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Departemen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kd_departemen")
    private Integer kdDepartemen;

    @Column(name = "nama_departemen", nullable = false, unique = true)
    private String namaDepartemen;
}
