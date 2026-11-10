package com.proyecto_semestral.ms_recetas.controller;

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
import com.proyecto_semestral.ms_recetas.dto.response.RecetaDetalleResponse;
import com.proyecto_semestral.ms_recetas.dto.response.RecetaResumenResponse;
import com.proyecto_semestral.ms_recetas.service.ImagenService;
import com.proyecto_semestral.ms_recetas.service.RecetaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecetaController {

    private final RecetaService recetaService;
    private final ImagenService imagenService;

    @GetMapping("/recipes")
    public Page<RecetaResumenResponse> listarPublicadas(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "0") int page) {
        return recetaService.listar(q, categoriaId, page);
    }

    @GetMapping("/recipes/{id}")
    public RecetaDetalleResponse obtenerPublicada(@PathVariable Long id) {
        return recetaService.obtenerPublicada(id);
    }

    @GetMapping("/admin/recipes")
    public List<RecetaResumenResponse> listarTodas() {
        return recetaService.listarTodas();
    }

    @PostMapping("/admin/recipes")
    public ResponseEntity<RecetaDetalleResponse> crear(
            @Valid @RequestBody CrearRecetaRequest request) {
        RecetaDetalleResponse receta = recetaService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/v1/admin/recipes/" + receta.id()))
                .body(receta);
    }

    @PutMapping("/admin/recipes/{id}")
    public RecetaDetalleResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarRecetaRequest request) {
        return recetaService.actualizar(id, request);
    }

    @DeleteMapping("/admin/recipes/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recetaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/admin/recipes/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecetaDetalleResponse> subirImagen(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile imagen) {
        RecetaDetalleResponse actual = recetaService.obtener(id);
        String nuevaUrl = imagenService.guardar(imagen);

        try {
            RecetaDetalleResponse actualizada = recetaService.actualizarImagen(id, nuevaUrl);
            imagenService.eliminar(actual.imagenUrl());
            return ResponseEntity.status(HttpStatus.OK).body(actualizada);
        } catch (RuntimeException exception) {
            imagenService.eliminar(nuevaUrl);
            throw exception;
        }
    }
}
