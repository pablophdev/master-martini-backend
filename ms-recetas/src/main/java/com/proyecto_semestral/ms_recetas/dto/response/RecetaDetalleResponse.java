package com.proyecto_semestral.ms_recetas.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record RecetaDetalleResponse(
        Long id,
        String titulo,
        String descripcion,
        String imagenUrl,
        String duracionTexto,
        CategoriaRecetaResponse categoria,
        String videoUrl,
        String rendimientoTexto,
        String insumosTitulo,
        String insumosDescripcion,
        List<IngredienteRecetaResponse> ingredientes,
        List<PasoPreparacionResponse> pasos) {
}
