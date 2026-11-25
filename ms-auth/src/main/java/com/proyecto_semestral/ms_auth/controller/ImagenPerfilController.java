package com.proyecto_semestral.ms_auth.controller;

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

import com.proyecto_semestral.ms_auth.entity.ImagenPerfil;
import com.proyecto_semestral.ms_auth.service.ImagenPerfilService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Imágenes")
@RestController
@RequestMapping("/imagenes/perfiles")
@RequiredArgsConstructor
public class ImagenPerfilController {

    private final ImagenPerfilService imagenService;

    @Operation(summary = "Descargar la foto de perfil de un usuario", description = "Público; no requiere token.")
    @ApiResponse(responseCode = "200", description = "Imagen almacenada; Content-Type según el archivo",
            content = @Content(mediaType = "image/*", schema = @Schema(type = "string", format = "binary")))
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.proyecto_semestral.ms_auth.dto.response.ApiErrorResponse.class)))
    @GetMapping("/{usuarioId}")
    public ResponseEntity<byte[]> descargar(@PathVariable Long usuarioId) {
        ImagenPerfil imagen = imagenService.cargar(usuarioId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imagen.getTipoContenido()))
                .cacheControl(CacheControl.noCache())
                .header("X-Content-Type-Options", "nosniff")
                .header("Content-Disposition", "inline; filename=\"" + imagen.getNombreArchivo() + "\"")
                .body(imagen.getDatos());
    }
}
