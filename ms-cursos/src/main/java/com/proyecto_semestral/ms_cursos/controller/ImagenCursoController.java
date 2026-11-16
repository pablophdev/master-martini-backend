package com.proyecto_semestral.ms_cursos.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_cursos.service.ImagenCursoService;
import com.proyecto_semestral.ms_cursos.entity.ImagenCurso;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/imagenes/cursos")
@RequiredArgsConstructor
public class ImagenCursoController {

    private final ImagenCursoService imagenService;

    @GetMapping("/{cursoId}")
    public ResponseEntity<byte[]> descargar(@PathVariable Long cursoId) {
        ImagenCurso imagen = imagenService.cargar(cursoId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(imagen.getTipoContenido()))
                .cacheControl(CacheControl.noCache())
                .header("X-Content-Type-Options", "nosniff")
                .header("Content-Disposition", "inline; filename=\"" + imagen.getNombreArchivo() + "\"")
                .body(imagen.getDatos());
    }
}
