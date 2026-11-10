package com.proyecto_semestral.ms_recetas.dto.response;

import lombok.Builder;

@Builder
public record RecetaResumenResponse(
        Long id,
        String titulo,
        String descripcion,
        String imagenUrl,
        String duracionTexto,
        CategoriaRecetaResponse categoria) {
}
