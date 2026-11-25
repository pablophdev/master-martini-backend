package com.proyecto_semestral.ms_cursos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_cursos.dto.response.CursoDetalleResponse;
import com.proyecto_semestral.ms_cursos.service.CursoService;

import lombok.RequiredArgsConstructor;

@Tag(name = "Administración de cursos")
@RestController
@RequestMapping("/api/v1/admin/cursos")
@RequiredArgsConstructor
public class AdminCursoController {

    private final CursoService cursoService;

    @Operation(summary = "Listar todos los cursos, incluidos borradores", description = "Requiere un JWT de usuario autenticado. A través del gateway requiere rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @GetMapping
    public List<CursoDetalleResponse> listarTodos() {
        return cursoService.listarTodos();
    }
}
