package com.proyecto_semestral.ms_cursos.dto.response;

import lombok.Builder;

@Builder
public record ObjetivoAprendizajeResponse(
        Long id,
        String descripcion,
        Integer orden) {
}
