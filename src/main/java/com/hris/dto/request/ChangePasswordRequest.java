package com.hris.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "passwordAsli wajib diisi")
        String passwordAsli,

        @NotBlank(message = "passwordBaru1 wajib diisi")
        @Size(min = 6, message = "passwordBaru1 minimal 6 karakter")
        String passwordBaru1,

        @NotBlank(message = "passwordBaru2 wajib diisi")
        @Size(min = 6, message = "passwordBaru2 minimal 6 karakter")
        String passwordBaru2
) {
}
