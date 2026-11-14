package com.proyecto_semestral.ms_cursos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CrearLeccionRequest(
        @NotBlank(message = "El título de la lección es obligatorio")
        @Size(max = 150, message = "El título de la lección no puede superar los 150 caracteres")
        String titulo,

        @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
        String descripcion,

        @NotNull(message = "El orden de la lección es obligatorio")
        @PositiveOrZero(message = "El orden de la lección no puede ser negativo")
        Integer orden,

        @Size(max = 500, message = "La URL del video no puede superar los 500 caracteres")
        String videoUrl,

        @PositiveOrZero(message = "La duración no puede ser negativa")
        Integer duracionSegundos) {
}
