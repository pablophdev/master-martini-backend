package com.proyecto_semestral.ms_cursos.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CrearCursoRequest(
        @NotBlank(message = "El título del curso es obligatorio")
        @Size(max = 200, message = "El título del curso no puede superar los 200 caracteres")
        String titulo,

        @NotBlank(message = "La descripción del curso es obligatoria")
        @Size(max = 2000, message = "La descripción no puede superar los 2000 caracteres")
        String descripcion,

        @NotNull(message = "La categoría es obligatoria")
        @Positive(message = "El id de la categoría debe ser mayor que 0")
        Long categoriaId,

        @Size(max = 2048, message = "La URL de la imagen no puede superar los 2048 caracteres")
        String imagenUrl,

        @Valid
        @Size(max = 100, message = "No se pueden enviar más de 100 objetivos")
        List<GuardarObjetivoRequest> objetivos) {
}
