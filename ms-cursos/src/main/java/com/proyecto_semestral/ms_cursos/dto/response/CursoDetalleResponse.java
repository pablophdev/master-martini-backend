package com.proyecto_semestral.ms_cursos.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record CursoDetalleResponse(
        Long id,
        String titulo,
        String descripcion,
        String imagenUrl,
        CategoriaCursoResponse categoria,
        List<ObjetivoAprendizajeResponse> objetivos,
        List<LeccionResponse> lecciones,
        Boolean publicado,
        Long duracionTotalSegundos) {
}
