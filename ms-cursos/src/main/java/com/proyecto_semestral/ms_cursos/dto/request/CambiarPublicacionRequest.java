package com.proyecto_semestral.ms_cursos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CambiarPublicacionRequest(
        @NotNull(message = "El estado de publicación es obligatorio")
        @Schema(example = "false")
        Boolean publicado) {
}
