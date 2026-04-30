package com.hris.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "email wajib diisi")
        @Email(message = "format email tidak valid")
        String email,

        @NotBlank(message = "password wajib diisi")
        String password,

        @NotBlank(message = "profile wajib diisi")
        String profile
) {
}
