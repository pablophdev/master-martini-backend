package com.proyecto_semestral.ms_auth.controller;

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

@RestController
@RequestMapping("/imagenes/perfiles")
@RequiredArgsConstructor
public class ImagenPerfilController {

    private final ImagenPerfilService imagenService;

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
