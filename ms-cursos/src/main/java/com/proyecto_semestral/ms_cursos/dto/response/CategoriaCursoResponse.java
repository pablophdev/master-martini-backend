package com.proyecto_semestral.ms_cursos.dto.response;

import lombok.Builder;

@Builder
public record CategoriaCursoResponse(
        Long id,
        String nombre) {
}
