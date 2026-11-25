package com.proyecto_semestral.ms_recetas.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto_semestral.ms_recetas.dto.request.ActualizarRecetaRequest;
import com.proyecto_semestral.ms_recetas.dto.request.CrearRecetaRequest;
import com.proyecto_semestral.ms_recetas.dto.response.CategoriaRecetaResponse;
import com.proyecto_semestral.ms_recetas.dto.response.RecetaDetalleResponse;
import com.proyecto_semestral.ms_recetas.dto.response.RecetaResumenResponse;
import com.proyecto_semestral.ms_recetas.service.ImagenService;
import com.proyecto_semestral.ms_recetas.service.RecetaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Recetas")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;
    private final ImagenService imagenService;

    @Operation(summary = "Listar recetas publicadas con búsqueda y filtro de categoría", description = "Público; no requiere token. Páginas desde 0, tamaño fijo de 12 recetas.")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @GetMapping("/recipes")
    public Page<RecetaResumenResponse> listarPublicadas(
            @Parameter(description = "Texto de búsqueda opcional") @RequestParam(required = false) String q,
            @Parameter(description = "Identificador de categoría") @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Índice de página, comenzando en 0") @RequestParam(defaultValue = "0") int page) {
        return recetaService.listar(q, categoriaId, page);
    }

    @Operation(summary = "Obtener el detalle de una receta publicada", description = "Público; no requiere token.")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @GetMapping("/recipes/{id}")
    public RecetaDetalleResponse obtenerPublicada(@PathVariable Long id) {
        return recetaService.obtenerPublicada(id);
    }

    @Operation(summary = "Listar categorías de recetas", description = "Público; no requiere token.")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @GetMapping("/recipes/categorias")
    public List<CategoriaRecetaResponse> listarCategorias() {
        return recetaService.listarCategorias();
    }

    @Operation(summary = "Listar todas las recetas, incluidos borradores", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @GetMapping("/admin/recipes")
    public List<RecetaResumenResponse> listarTodas() {
        return recetaService.listarTodas();
    }

    @Operation(summary = "Obtener una receta para administración", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @GetMapping("/admin/recipes/{id}")
    public RecetaDetalleResponse obtenerParaAdministrar(@PathVariable Long id) {
        return recetaService.obtener(id);
    }

    @Operation(summary = "Crear una receta", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "201", description = "Creado correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @PostMapping("/admin/recipes")
    public ResponseEntity<RecetaDetalleResponse> crear(
            @Valid @RequestBody CrearRecetaRequest request) {
        RecetaDetalleResponse receta = recetaService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/recipes/" + receta.id()))
                .body(receta);
    }

    @Operation(summary = "Actualizar una receta y sus ingredientes y pasos", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @PutMapping("/admin/recipes/{id}")
    public RecetaDetalleResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarRecetaRequest request) {
        return recetaService.actualizar(id, request);
    }

    @Operation(summary = "Eliminar una receta", description = "Requiere un JWT con rol ADMIN.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Eliminado correctamente", content = @Content)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @DeleteMapping("/admin/recipes/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recetaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Subir o reemplazar la imagen de una receta", description = "Requiere un JWT con rol ADMIN. Enviar el archivo en el campo image (multipart/form-data). JPEG, PNG, GIF, WebP, AVIF o HEIC. Límite: 5 MB.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content)
    @ApiResponse(responseCode = "413", description = "El archivo supera el límite configurado", content = @Content)
    @PostMapping(
            value = "/admin/recipes/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecetaDetalleResponse> subirImagen(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imagen) {
        recetaService.obtener(id);
        String nuevaUrl = imagenService.guardar(id, imagen);
        RecetaDetalleResponse actualizada = recetaService.actualizarImagen(id, nuevaUrl);
        return ResponseEntity.status(HttpStatus.OK).body(actualizada);
    }
}
