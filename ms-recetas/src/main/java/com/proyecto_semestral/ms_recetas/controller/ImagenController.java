package com.proyecto_semestral.ms_recetas.controller;

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

@RestController
@RequestMapping("/imagenes/recetas")
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenService imagenService;

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
