package com.proyecto_semestral.ms_cursos.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CambiarPublicacionRequest(
        @NotNull(message = "El estado de publicación es obligatorio")
        Boolean publicado) {
}
