package com.proyecto_semestral.ms_cursos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaCursoController {

    private final CategoriaCursoService categoriaService;

    @GetMapping
    public List<CategoriaCursoResponse> listar() {
        return categoriaService.listar();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaCursoResponse> crear(
            @Valid @RequestBody GuardarCategoriaRequest request) {
        CategoriaCursoResponse categoria = categoriaService.crear(request);
        return ResponseEntity
                .created(URI.create("/api/categorias/" + categoria.id()))
                .body(categoria);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoriaCursoResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody GuardarCategoriaRequest request) {
        return categoriaService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
