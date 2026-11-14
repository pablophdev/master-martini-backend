package com.proyecto_semestral.ms_cursos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_semestral.ms_cursos.dto.request.ActualizarLeccionRequest;
import com.proyecto_semestral.ms_cursos.dto.request.CrearLeccionRequest;
import com.proyecto_semestral.ms_cursos.dto.response.LeccionResponse;
import com.proyecto_semestral.ms_cursos.entity.Leccion;
import com.proyecto_semestral.ms_cursos.repository.CursoRepository;
import com.proyecto_semestral.ms_cursos.repository.LeccionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeccionService {

    private final LeccionRepository leccionRepository;
    private final CursoRepository cursoRepository;

    @Transactional
    public LeccionResponse crear(Long cursoId, CrearLeccionRequest request) {
        verificarCurso(cursoId);
        validarDuracionYOrden(request.duracionSegundos(), request.orden());

        if (leccionRepository.existsByCursoIdAndOrden(cursoId, request.orden())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una lección con ese orden en el curso");
        }

        Leccion leccion = Leccion.builder()
                .cursoId(cursoId)
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .orden(request.orden())
                .videoUrl(request.videoUrl())
                .duracionSegundos(request.duracionSegundos())
                .build();

        return toResponse(leccionRepository.save(leccion));
    }

    @Transactional
    public LeccionResponse actualizar(Long cursoId, Long leccionId, ActualizarLeccionRequest request) {
        verificarCurso(cursoId);
        Leccion leccion = buscar(cursoId, leccionId);
        validarDuracionYOrden(request.duracionSegundos(), request.orden());

        if (leccionRepository.existsByCursoIdAndOrdenAndIdNot(cursoId, request.orden(), leccionId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ya existe una lección con ese orden en el curso");
        }

        leccion.setTitulo(request.titulo());
        leccion.setDescripcion(request.descripcion());
        leccion.setOrden(request.orden());
        leccion.setVideoUrl(request.videoUrl());
        leccion.setDuracionSegundos(request.duracionSegundos());

        return toResponse(leccionRepository.save(leccion));
    }

    @Transactional(readOnly = true)
    public List<LeccionResponse> listarPorCurso(Long cursoId) {
        verificarCurso(cursoId);
        return leccionRepository.findByCursoIdOrderByOrdenAsc(cursoId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long cursoId, Long leccionId) {
        verificarCurso(cursoId);
        leccionRepository.delete(buscar(cursoId, leccionId));
    }

    private void verificarCurso(Long cursoId) {
        if (!cursoRepository.existsById(cursoId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado con id " + cursoId);
        }
    }

    private Leccion buscar(Long cursoId, Long leccionId) {
        return leccionRepository.findByIdAndCursoId(leccionId, cursoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Lección no encontrada con id " + leccionId));
    }

    private void validarDuracionYOrden(Integer duracionSegundos, Integer orden) {
        if (orden == null || orden < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El orden no puede ser negativo");
        }
        if (duracionSegundos != null && duracionSegundos < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La duración no puede ser negativa");
        }
    }

    private LeccionResponse toResponse(Leccion leccion) {
        return LeccionResponse.builder()
                .id(leccion.getId())
                .titulo(leccion.getTitulo())
                .descripcion(leccion.getDescripcion())
                .orden(leccion.getOrden())
                .videoUrl(leccion.getVideoUrl())
                .duracionSegundos(leccion.getDuracionSegundos())
                .build();
    }
}
