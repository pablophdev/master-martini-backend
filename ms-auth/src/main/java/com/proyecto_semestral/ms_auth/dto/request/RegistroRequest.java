package com.proyecto_semestral.ms_auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
        @Schema(example = "Nombre de ejemplo")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 254, message = "El email no puede superar los 254 caracteres")
        @Schema(example = "persona@example.com")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Schema(example = "EjemploSeguro2026", format = "password", accessMode = Schema.AccessMode.WRITE_ONLY)
        String password,

        @Size(max = 150, message = "El nombre del negocio no puede superar los 150 caracteres")
        @Schema(example = "Pastelería de ejemplo")
        String nombreNegocio,

        @Size(max = 100, message = "El tipo de negocio no puede superar los 100 caracteres")
        String tipoNegocio,

        @Size(max = 50, message = "El tamaño del negocio no puede superar los 50 caracteres")
        String tamanoNegocio) {
}
