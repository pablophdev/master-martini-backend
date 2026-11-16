package com.proyecto_semestral.ms_recetas.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_recetas.service.ImagenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/imagenes/recetas")
@RequiredArgsConstructor
public class ImagenController {

    private final ImagenService imagenService;

    @GetMapping("/{nombreArchivo:.+}")
    public ResponseEntity<Resource> descargar(@PathVariable String nombreArchivo) {
        Resource imagen = imagenService.cargar(nombreArchivo);
        return ResponseEntity.ok()
                .contentType(imagenService.obtenerTipoContenido(nombreArchivo))
                .cacheControl(CacheControl.noCache())
                .header("X-Content-Type-Options", "nosniff")
                .body(imagen);
    }
}
