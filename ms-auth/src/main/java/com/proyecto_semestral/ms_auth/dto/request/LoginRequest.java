package com.proyecto_semestral.ms_auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Schema(example = "persona@example.com")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Schema(example = "EjemploSeguro2026", format = "password", accessMode = Schema.AccessMode.WRITE_ONLY)
        String password) {
}
