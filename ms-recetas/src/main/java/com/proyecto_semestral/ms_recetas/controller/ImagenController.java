package com.proyecto_semestral.ms_recetas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_recetas.service.ImagenService;
import com.proyecto_semestral.ms_recetas.entity.ImagenReceta;

import lombok.RequiredArgsConstructor;

@Tag(name = "Imágenes")
@RestController
@RequestMapping("/imagenes/recetas")
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenService imagenService;

    @Operation(summary = "Descargar la imagen de una receta", description = "Público; no requiere token.")
    @ApiResponse(responseCode = "200", description = "Imagen almacenada; Content-Type según el archivo",
            content = @Content(mediaType = "image/*", schema = @Schema(type = "string", format = "binary")))
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @GetMapping("/{recetaId}")
    public ResponseEntity<byte[]> descargar(@PathVariable Long recetaId) {
        ImagenReceta imagen = imagenService.cargar(recetaId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imagen.getTipoContenido()))
                .cacheControl(CacheControl.noCache())
                .header("X-Content-Type-Options", "nosniff")
                .header("Content-Disposition", "inline; filename=\"" + imagen.getNombreArchivo() + "\"")
                .body(imagen.getDatos());
    }
}
