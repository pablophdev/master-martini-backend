package com.proyecto_semestral.ms_cursos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_semestral.ms_cursos.dto.request.GuardarCategoriaRequest;
import com.proyecto_semestral.ms_cursos.dto.response.CategoriaCursoResponse;
import com.proyecto_semestral.ms_cursos.entity.CategoriaCurso;
import com.proyecto_semestral.ms_cursos.repository.CategoriaCursoRepository;
import com.proyecto_semestral.ms_cursos.repository.CursoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaCursoService {

    private final CategoriaCursoRepository categoriaRepository;
    private final CursoRepository cursoRepository;

    @Transactional
    public CategoriaCursoResponse crear(GuardarCategoriaRequest request) {
        String nombre = normalizarNombre(request.nombre());
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        }

        CategoriaCurso categoria = CategoriaCurso.builder()
                .nombre(nombre)
                .build();
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaCursoResponse actualizar(Long id, GuardarCategoriaRequest request) {
        CategoriaCurso categoria = buscar(id);
        String nombre = normalizarNombre(request.nombre());

        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre");
        }

        categoria.setNombre(nombre);
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional(readOnly = true)
    public List<CategoriaCursoResponse> listar() {
        return categoriaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long id) {
        CategoriaCurso categoria = buscar(id);
        if (cursoRepository.existsByCategoriaId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una categoría que tiene cursos asociados");
        }
        categoriaRepository.delete(categoria);
    }

    private CategoriaCurso buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada con id " + id));
    }

    private String normalizarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la categoría es obligatorio");
        }
        return nombre.trim();
    }

    private CategoriaCursoResponse toResponse(CategoriaCurso categoria) {
        return CategoriaCursoResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .build();
    }
}
