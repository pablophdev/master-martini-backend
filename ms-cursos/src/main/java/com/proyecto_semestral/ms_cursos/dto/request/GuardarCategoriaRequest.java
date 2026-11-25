package com.proyecto_semestral.ms_cursos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record GuardarCategoriaRequest(
        @NotBlank(message = "El nombre de la categoría es obligatorio")
        @Size(max = 100, message = "El nombre de la categoría no puede superar los 100 caracteres")
        @Schema(example = "Nombre de ejemplo")
        String nombre) {
}
