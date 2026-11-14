package com.proyecto_semestral.ms_cursos.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_cursos.dto.request.ActualizarLeccionRequest;
import com.proyecto_semestral.ms_cursos.dto.request.CrearLeccionRequest;
import com.proyecto_semestral.ms_cursos.dto.response.LeccionResponse;
import com.proyecto_semestral.ms_cursos.service.LeccionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cursos/{cursoId}/lecciones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LeccionController {

    private final LeccionService leccionService;

    @PostMapping
    public ResponseEntity<LeccionResponse> crear(
            @PathVariable Long cursoId,
            @Valid @RequestBody CrearLeccionRequest request) {
        LeccionResponse leccion = leccionService.crear(cursoId, request);
        URI ubicacion = URI.create("/api/cursos/" + cursoId + "/lecciones/" + leccion.id());
        return ResponseEntity.created(ubicacion).body(leccion);
    }

    @PutMapping("/{id}")
    public LeccionResponse actualizar(
            @PathVariable Long cursoId,
            @PathVariable Long id,
            @Valid @RequestBody ActualizarLeccionRequest request) {
        return leccionService.actualizar(cursoId, id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long cursoId,
            @PathVariable Long id) {
        leccionService.eliminar(cursoId, id);
        return ResponseEntity.noContent().build();
    }
}
