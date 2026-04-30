package com.hris.dto.request;

import jakarta.validation.constraints.NotNull;

public record SubmitAbsensiRequest(
        @NotNull(message = "tglAbsensi wajib diisi")
        Long tglAbsensi,

        @NotNull(message = "kdStatus wajib diisi")
        Integer kdStatus
) {
}
