package com.proyecto_semestral.ms_cursos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record GuardarObjetivoRequest(
        @NotBlank(message = "La descripción del objetivo es obligatoria")
        @Size(max = 500, message = "La descripción del objetivo no puede superar los 500 caracteres")
        String descripcion,

        @NotNull(message = "El orden del objetivo es obligatorio")
        @PositiveOrZero(message = "El orden del objetivo no puede ser negativo")
        Integer orden) {
}
