package com.proyecto_semestral.ms_cursos.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_cursos.service.ImagenCursoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/imagenes/cursos")
@RequiredArgsConstructor
public class ImagenCursoController {

    private final ImagenCursoService imagenService;

    @GetMapping("/{nombreArchivo:.+}")
    public ResponseEntity<Resource> descargar(@PathVariable String nombreArchivo) {
        Resource imagen = imagenService.cargar(nombreArchivo);
        return ResponseEntity.ok()
                .contentType(imagenService.tipoContenido(nombreArchivo))
                .cacheControl(CacheControl.noCache())
                .header("X-Content-Type-Options", "nosniff")
                .body(imagen);
    }
}
