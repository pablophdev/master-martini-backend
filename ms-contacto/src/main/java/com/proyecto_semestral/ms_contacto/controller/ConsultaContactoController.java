package com.proyecto_semestral.ms_contacto.controller;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_contacto.dto.request.ActualizarEstadoConsultaRequest;
import com.proyecto_semestral.ms_contacto.dto.request.CrearConsultaRequest;
import com.proyecto_semestral.ms_contacto.dto.response.ConsultaResponse;
import com.proyecto_semestral.ms_contacto.service.ConsultaContactoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Contacto")
@RestController
@RequiredArgsConstructor
public class ConsultaContactoController {

    private final ConsultaContactoService consultaService;

    @Operation(summary = "Enviar una consulta de contacto", description = "Público; no requiere token.")
    @ApiResponse(responseCode = "201", description = "Creado correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @PostMapping("/api/contacto")
    public ResponseEntity<ConsultaResponse> crear(
            @Valid @RequestBody CrearConsultaRequest request) {
        ConsultaResponse consulta = consultaService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/consultas/" + consulta.id()))
                .body(consulta);
    }

    @Operation(summary = "Listar consultas de contacto", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @GetMapping("/api/consultas")
    public List<ConsultaResponse> listar() {
        return consultaService.listar();
    }

    @Operation(summary = "Obtener una consulta de contacto", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @GetMapping("/api/consultas/{id}")
    public ConsultaResponse obtener(@PathVariable Long id) {
        return consultaService.obtener(id);
    }

    @Operation(summary = "Cambiar el estado de una consulta", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @PatchMapping("/api/consultas/{id}/estado")
    public ConsultaResponse actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoConsultaRequest request) {
        return consultaService.actualizarEstado(id, request.estado());
    }

    @Operation(summary = "Eliminar una consulta", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @DeleteMapping("/api/consultas/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        consultaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
