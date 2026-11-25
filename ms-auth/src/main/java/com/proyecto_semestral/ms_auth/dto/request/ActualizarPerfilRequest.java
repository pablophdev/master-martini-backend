package com.proyecto_semestral.ms_auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ActualizarPerfilRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
        @Schema(example = "Nombre de ejemplo")
        String nombre,

        @Size(max = 150, message = "El nombre del negocio no puede superar los 150 caracteres")
        @Schema(example = "Pastelería de ejemplo")
        String nombreNegocio,

        @Size(max = 100, message = "El tipo de negocio no puede superar los 100 caracteres")
        String tipoNegocio,

        @Size(max = 50, message = "El tamaño del negocio no puede superar los 50 caracteres")
        String tamanoNegocio) {
}
