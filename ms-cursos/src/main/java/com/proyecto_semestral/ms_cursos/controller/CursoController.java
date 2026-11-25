package com.proyecto_semestral.ms_cursos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_semestral.ms_cursos.dto.request.ActualizarCursoRequest;
import com.proyecto_semestral.ms_cursos.dto.request.CambiarPublicacionRequest;
import com.proyecto_semestral.ms_cursos.dto.request.CrearCursoRequest;
import com.proyecto_semestral.ms_cursos.dto.response.CursoDetalleResponse;
import com.proyecto_semestral.ms_cursos.dto.response.CursoResumenResponse;
import com.proyecto_semestral.ms_cursos.service.CursoService;
import com.proyecto_semestral.ms_cursos.service.ImagenCursoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Cursos")
@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;
    private final ImagenCursoService imagenService;

    @Operation(summary = "Listar cursos publicados con búsqueda, categoría y paginación", description = "Público; no requiere token. Páginas desde 0; size entre 1 y 100 (por defecto 12).")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @GetMapping
    public Page<CursoResumenResponse> listarPublicados(
            @Parameter(description = "Texto de búsqueda opcional") @RequestParam(required = false) String q,
            @Parameter(description = "Identificador de categoría") @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Índice de página, comenzando en 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de cursos por página, entre 1 y 100") @RequestParam(defaultValue = "12") int size) {
        return cursoService.listarPublicados(q, categoriaId, page, size);
    }

    @Operation(summary = "Obtener un curso publicado con objetivos y lecciones", description = "Público; no requiere token.")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @GetMapping("/{id}")
    public CursoDetalleResponse obtenerDetalle(@PathVariable Long id) {
        return cursoService.obtenerDetallePublicado(id);
    }

    @Operation(summary = "Crear un curso en borrador", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Creado correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @PostMapping
    public ResponseEntity<CursoDetalleResponse> crear(@Valid @RequestBody CrearCursoRequest request) {
        CursoDetalleResponse curso = cursoService.crear(request);
        return ResponseEntity.created(URI.create("/api/cursos/" + curso.id())).body(curso);
    }

    @Operation(summary = "Actualizar un curso y sus objetivos", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @PutMapping("/{id}")
    public CursoDetalleResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarCursoRequest request) {
        return cursoService.actualizar(id, request);
    }

    @Operation(summary = "Subir o reemplazar la imagen de un curso", description = "Requiere un JWT con rol ADMIN. Enviar el archivo en el campo image (multipart/form-data). JPEG, PNG, GIF, WebP, AVIF o HEIC. Límite: 5 MB.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @ApiResponse(responseCode = "413", description = "El archivo supera el límite configurado", content = @Content)
    @PostMapping(
            value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CursoDetalleResponse> subirImagen(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imagen) {
        cursoService.obtenerDetalle(id);
        String nuevaUrl = imagenService.guardar(id, imagen);
        CursoDetalleResponse actualizado = cursoService.actualizarImagen(id, nuevaUrl);
        return ResponseEntity.status(HttpStatus.OK).body(actualizado);
    }

    @Operation(summary = "Publicar o despublicar un curso", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @PatchMapping("/{id}/publicacion")
    public CursoDetalleResponse cambiarPublicacion(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPublicacionRequest request) {
        return cursoService.cambiarPublicacion(id, request);
    }

    @Operation(summary = "Eliminar un curso", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
