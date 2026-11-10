package com.proyecto_semestral.ms_recetas.dto.response;

import lombok.Builder;

@Builder
public record IngredienteRecetaResponse(
        Long id,
        String nombre,
        String cantidadTexto,
        Integer orden) {
}
