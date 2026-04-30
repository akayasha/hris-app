package com.hris.dto.request;

import jakarta.validation.constraints.NotBlank;

public record InitDataRequest(
        @NotBlank(message = "namaAdmin wajib diisi")
        String namaAdmin,

        @NotBlank(message = "perusahaan wajib diisi")
        String perusahaan
) {
}
