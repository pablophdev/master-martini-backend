package com.proyecto_semestral.ms_cursos.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_cursos.dto.request.ActualizarCursoRequest;
import com.proyecto_semestral.ms_cursos.dto.request.CambiarPublicacionRequest;
import com.proyecto_semestral.ms_cursos.dto.request.CrearCursoRequest;
import com.proyecto_semestral.ms_cursos.dto.response.CursoDetalleResponse;
import com.proyecto_semestral.ms_cursos.dto.response.CursoResumenResponse;
import com.proyecto_semestral.ms_cursos.service.CursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    public Page<CursoResumenResponse> listarPublicados(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return cursoService.listarPublicados(q, categoriaId, page, size);
    }

    @GetMapping("/{id}")
    public CursoDetalleResponse obtenerDetalle(@PathVariable Long id) {
        return cursoService.obtenerDetallePublicado(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CursoDetalleResponse> crear(@Valid @RequestBody CrearCursoRequest request) {
        CursoDetalleResponse curso = cursoService.crear(request);
        return ResponseEntity.created(URI.create("/api/cursos/" + curso.id())).body(curso);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CursoDetalleResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarCursoRequest request) {
        return cursoService.actualizar(id, request);
    }

    @PatchMapping("/{id}/publicacion")
    @PreAuthorize("hasRole('ADMIN')")
    public CursoDetalleResponse cambiarPublicacion(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPublicacionRequest request) {
        return cursoService.cambiarPublicacion(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
