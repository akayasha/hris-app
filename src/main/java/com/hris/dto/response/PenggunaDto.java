package com.hris.dto.response;

import com.hris.entity.Pengguna;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PenggunaDto {
    private String profile;
    private String idUser;
    private String namaLengkap;
    private String tempatLahir;
    private Long tanggalLahir;
    private String email;
    private String password;
    private String nikUser;
    private Integer kdJabatan;
    private String namaJabatan;
    private Integer kdDepartemen;
    private String namaDepartemen;
    private Integer kdUnitKerja;
    private String namaUnitKerja;
    private Integer kdJenisKelamin;
    private String namaJenisKelamin;
    private Integer kdPendidikan;
    private String namaPendidikan;
    private String photo;

    public static PenggunaDto from(Pengguna p) {
        return PenggunaDto.builder()
                .profile(p.getProfile())
                .idUser(p.getIdUser())
                .namaLengkap(p.getNamaLengkap())
                .tempatLahir(p.getTempatLahir())
                .tanggalLahir(p.getTanggalLahir())
                .email(p.getEmail())
                .password(p.getPassword())
                .nikUser(p.getNikUser())
                .kdJabatan(p.getJabatan() != null ? p.getJabatan().getKdJabatan() : null)
                .namaJabatan(p.getJabatan() != null ? p.getJabatan().getNamaJabatan() : null)
                .kdDepartemen(p.getDepartemen() != null ? p.getDepartemen().getKdDepartemen() : null)
                .namaDepartemen(p.getDepartemen() != null ? p.getDepartemen().getNamaDepartemen() : null)
                .kdUnitKerja(p.getUnitKerja() != null ? p.getUnitKerja().getKdUnitKerja() : null)
                .namaUnitKerja(p.getUnitKerja() != null ? p.getUnitKerja().getNamaUnitKerja() : null)
                .kdJenisKelamin(p.getJenisKelamin() != null ? p.getJenisKelamin().getKdJenisKelamin() : null)
                .namaJenisKelamin(p.getJenisKelamin() != null ? p.getJenisKelamin().getNamaJenisKelamin() : null)
                .kdPendidikan(p.getPendidikan() != null ? p.getPendidikan().getKdPendidikan() : null)
                .namaPendidikan(p.getPendidikan() != null ? p.getPendidikan().getNamaPendidikan() : null)
                .photo(p.getPhoto())
                .build();
    }
}
