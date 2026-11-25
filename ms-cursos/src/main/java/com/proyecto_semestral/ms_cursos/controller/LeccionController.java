package com.proyecto_semestral.ms_cursos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

import org.springframework.http.ResponseEntity;
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

@Tag(name = "Lecciones")
@RestController
@RequestMapping("/api/cursos/{cursoId}/lecciones")
@RequiredArgsConstructor
public class LeccionController {

    private final LeccionService leccionService;

    @Operation(summary = "Agregar una lección a un curso", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Creado correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto con un registro existente", content = @Content)
    @PostMapping
    public ResponseEntity<LeccionResponse> crear(
            @PathVariable Long cursoId,
            @Valid @RequestBody CrearLeccionRequest request) {
        LeccionResponse leccion = leccionService.crear(cursoId, request);
        URI ubicacion = URI.create("/api/cursos/" + cursoId + "/lecciones/" + leccion.id());
        return ResponseEntity.created(ubicacion).body(leccion);
    }

    @Operation(summary = "Actualizar una lección de un curso", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto con un registro existente", content = @Content)
    @PutMapping("/{id}")
    public LeccionResponse actualizar(
            @PathVariable Long cursoId,
            @PathVariable Long id,
            @Valid @RequestBody ActualizarLeccionRequest request) {
        return leccionService.actualizar(cursoId, id, request);
    }

    @Operation(summary = "Eliminar una lección de un curso", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long cursoId,
            @PathVariable Long id) {
        leccionService.eliminar(cursoId, id);
        return ResponseEntity.noContent().build();
    }
}
