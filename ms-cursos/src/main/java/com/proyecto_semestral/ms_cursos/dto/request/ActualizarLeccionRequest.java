package com.proyecto_semestral.ms_cursos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ActualizarLeccionRequest(
        @NotBlank(message = "El título de la lección es obligatorio")
        @Size(max = 200, message = "El título de la lección no puede superar los 200 caracteres")
        String titulo,

        @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
        String descripcion,

        @NotNull(message = "El orden de la lección es obligatorio")
        @Positive(message = "El orden de la lección debe ser mayor que cero")
        Integer orden,

        @NotBlank(message = "La URL del video es obligatoria")
        @Size(max = 2048, message = "La URL del video no puede superar los 2048 caracteres")
        String videoUrl,

        @NotNull(message = "La duración es obligatoria")
        @Positive(message = "La duración debe ser mayor que cero")
        Integer duracionSegundos) {
}
