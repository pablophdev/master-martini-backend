package com.proyecto_semestral.ms_recetas.dto.response;

import lombok.Builder;

@Builder
public record PasoPreparacionResponse(
        Long id,
        Integer numero,
        String descripcion) {
}
