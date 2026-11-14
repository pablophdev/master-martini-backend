package com.proyecto_semestral.ms_cursos.dto.response;

import lombok.Builder;

@Builder
public record LeccionResponse(
        Long id,
        String titulo,
        String descripcion,
        Integer orden,
        String videoUrl,
        Integer duracionSegundos) {
}
