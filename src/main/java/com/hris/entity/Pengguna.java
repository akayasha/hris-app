package com.hris.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pengguna")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pengguna implements UserDetails {

    @Id
    @Column(name = "id_user", length = 36)
    private String idUser;

    @Column(name = "nik_user", unique = true)
    private String nikUser;

    @Column(name = "nama_lengkap", nullable = false)
    private String namaLengkap;

    @Column(name = "tempat_lahir")
    private String tempatLahir;

    @Column(name = "tanggal_lahir")
    private Long tanggalLahir;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    // "ADMIN" atau "PEGAWAI"
    @Column(name = "profile", nullable = false)
    private String profile;

    @Column(name = "photo")
    private String photo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kd_jabatan")
    private Jabatan jabatan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kd_departemen")
    private Departemen departemen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kd_unit_kerja")
    private UnitKerja unitKerja;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kd_jenis_kelamin")
    private JenisKelamin jenisKelamin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kd_pendidikan")
    private Pendidikan pendidikan;

    @PrePersist
    public void prePersist() {
        if (this.idUser == null || this.idUser.isBlank()) {
            this.idUser = UUID.randomUUID().toString();
        }
    }

    // --- UserDetails ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + profile));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
