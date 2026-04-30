package com.hris.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePegawaiRequest(
        @NotBlank(message = "namaLengkap wajib diisi")
        String namaLengkap,

        @NotBlank(message = "email wajib diisi")
        @Email(message = "format email tidak valid")
        String email,

        @NotBlank(message = "tempatLahir wajib diisi")
        String tempatLahir,

        @NotNull(message = "tanggalLahir wajib diisi")
        Long tanggalLahir,

        @NotNull(message = "kdJenisKelamin wajib diisi")
        Integer kdJenisKelamin,

        @NotNull(message = "kdPendidikan wajib diisi")
        Integer kdPendidikan,

        @NotNull(message = "kdJabatan wajib diisi")
        Integer kdJabatan,

        @NotNull(message = "kdDepartemen wajib diisi")
        Integer kdDepartemen,

        @NotNull(message = "kdUnitKerja wajib diisi")
        Integer kdUnitKerja,

        @NotBlank(message = "password wajib diisi")
        @Size(min = 6, message = "password minimal 6 karakter")
        String password,

        @NotBlank(message = "passwordC wajib diisi")
        @Size(min = 6, message = "passwordC minimal 6 karakter")
        String passwordC
) {
}
