package com.proyecto_semestral.ms_recetas.dto.response;

import lombok.Builder;

@Builder
public record CategoriaRecetaResponse(
        Long id,
        String nombre) {
}
