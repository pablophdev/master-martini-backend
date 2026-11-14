package com.proyecto_semestral.ms_cursos.dto.response;

import lombok.Builder;

@Builder
public record CursoResumenResponse(
        Long id,
        String titulo,
        String descripcion,
        String imagenUrl,
        CategoriaCursoResponse categoria,
        Integer cantidadLecciones,
        Long duracionTotalSegundos) {
}
