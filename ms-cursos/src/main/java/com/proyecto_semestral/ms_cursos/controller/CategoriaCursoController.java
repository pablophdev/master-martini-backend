package com.proyecto_semestral.ms_cursos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_cursos.dto.request.GuardarCategoriaRequest;
import com.proyecto_semestral.ms_cursos.dto.response.CategoriaCursoResponse;
import com.proyecto_semestral.ms_cursos.service.CategoriaCursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Categorías de cursos")
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaCursoController {

    private final CategoriaCursoService categoriaService;

    @Operation(summary = "Listar categorías de cursos", description = "Público; no requiere token.")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @GetMapping
    public List<CategoriaCursoResponse> listar() {
        return categoriaService.listar();
    }

    @Operation(summary = "Crear una categoría de cursos", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Creado correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto con un registro existente", content = @Content)
    @PostMapping
    public ResponseEntity<CategoriaCursoResponse> crear(
            @Valid @RequestBody GuardarCategoriaRequest request) {
        CategoriaCursoResponse categoria = categoriaService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/categorias/" + categoria.id()))
                .body(categoria);
    }

    @Operation(summary = "Actualizar una categoría de cursos", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto con un registro existente", content = @Content)
    @PutMapping("/{id}")
    public CategoriaCursoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody GuardarCategoriaRequest request) {
        return categoriaService.actualizar(id, request);
    }

    @Operation(summary = "Eliminar una categoría de cursos", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @ApiResponse(responseCode = "409", description = "Conflicto con un registro existente", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
