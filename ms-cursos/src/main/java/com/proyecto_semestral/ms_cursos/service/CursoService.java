package com.proyecto_semestral.ms_cursos.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_semestral.ms_cursos.dto.request.ActualizarCursoRequest;
import com.proyecto_semestral.ms_cursos.dto.request.CambiarPublicacionRequest;
import com.proyecto_semestral.ms_cursos.dto.request.CrearCursoRequest;
import com.proyecto_semestral.ms_cursos.dto.request.GuardarObjetivoRequest;
import com.proyecto_semestral.ms_cursos.dto.response.CategoriaCursoResponse;
import com.proyecto_semestral.ms_cursos.dto.response.CursoDetalleResponse;
import com.proyecto_semestral.ms_cursos.dto.response.CursoResumenResponse;
import com.proyecto_semestral.ms_cursos.dto.response.LeccionResponse;
import com.proyecto_semestral.ms_cursos.dto.response.ObjetivoAprendizajeResponse;
import com.proyecto_semestral.ms_cursos.entity.CategoriaCurso;
import com.proyecto_semestral.ms_cursos.entity.Curso;
import com.proyecto_semestral.ms_cursos.entity.Leccion;
import com.proyecto_semestral.ms_cursos.entity.ObjetivoAprendizaje;
import com.proyecto_semestral.ms_cursos.repository.CategoriaCursoRepository;
import com.proyecto_semestral.ms_cursos.repository.CursoRepository;
import com.proyecto_semestral.ms_cursos.repository.LeccionRepository;
import com.proyecto_semestral.ms_cursos.repository.ObjetivoAprendizajeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final CategoriaCursoRepository categoriaRepository;
    private final LeccionRepository leccionRepository;
    private final ObjetivoAprendizajeRepository objetivoRepository;

    @Transactional
    public CursoDetalleResponse crear(CrearCursoRequest request) {
        buscarCategoria(request.categoriaId());

        Curso curso = Curso.builder()
                .titulo(request.titulo())
                .descripcion(request.descripcion())
                .categoriaId(request.categoriaId())
                .imagenUrl(request.imagenUrl())
                .publicado(false)
                .build();

        Curso guardado = cursoRepository.save(curso);
        guardarObjetivos(guardado.getId(), request.objetivos());
        return construirDetalle(guardado);
    }

    @Transactional
    public CursoDetalleResponse actualizar(Long id, ActualizarCursoRequest request) {
        Curso curso = buscarCurso(id);
        buscarCategoria(request.categoriaId());

        curso.setTitulo(request.titulo());
        curso.setDescripcion(request.descripcion());
        curso.setCategoriaId(request.categoriaId());
        curso.setImagenUrl(request.imagenUrl());

        Curso guardado = cursoRepository.save(curso);

        if (request.objetivos() != null) {
            objetivoRepository.deleteByCursoId(id);
            objetivoRepository.flush();
            guardarObjetivos(id, request.objetivos());
        }

        return construirDetalle(guardado);
    }

    @Transactional(readOnly = true)
    public List<CursoResumenResponse> listarPublicados() {
        return listarPublicados(null, null);
    }

    @Transactional(readOnly = true)
    public List<CursoResumenResponse> listarPublicados(String busqueda, Long categoriaId) {
        List<Curso> cursos = buscarPublicados(busqueda, categoriaId);
        Map<Long, CategoriaCurso> categorias = categoriaRepository.findAll().stream()
                .collect(Collectors.toMap(CategoriaCurso::getId, Function.identity()));

        return cursos.stream()
                .map(curso -> construirResumen(curso, categorias.get(curso.getCategoriaId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CursoDetalleResponse obtenerDetalle(Long id) {
        return construirDetalle(buscarCurso(id));
    }

    @Transactional
    public CursoDetalleResponse cambiarPublicacion(Long id, CambiarPublicacionRequest request) {
        if (request.publicado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado de publicación es obligatorio");
        }

        Curso curso = buscarCurso(id);
        curso.setPublicado(request.publicado());
        return construirDetalle(cursoRepository.save(curso));
    }

    @Transactional
    public void eliminar(Long id) {
        Curso curso = buscarCurso(id);
        objetivoRepository.deleteByCursoId(id);
        leccionRepository.deleteByCursoId(id);
        cursoRepository.delete(curso);
    }

    private List<Curso> buscarPublicados(String busqueda, Long categoriaId) {
        boolean tieneBusqueda = busqueda != null && !busqueda.isBlank();

        if (tieneBusqueda && categoriaId != null) {
            return cursoRepository.findByTituloContainingIgnoreCaseAndCategoriaIdAndPublicadoTrue(
                    busqueda.trim(), categoriaId);
        }
        if (tieneBusqueda) {
            return cursoRepository.findByTituloContainingIgnoreCaseAndPublicadoTrue(busqueda.trim());
        }
        if (categoriaId != null) {
            return cursoRepository.findByCategoriaIdAndPublicadoTrue(categoriaId);
        }
        return cursoRepository.findByPublicadoTrue();
    }

    private Curso buscarCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Curso no encontrado con id " + id));
    }

    private CategoriaCurso buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Categoría no encontrada con id " + id));
    }

    private void guardarObjetivos(Long cursoId, List<GuardarObjetivoRequest> objetivos) {
        if (objetivos == null || objetivos.isEmpty()) {
            return;
        }

        List<ObjetivoAprendizaje> entidades = new ArrayList<>();
        for (GuardarObjetivoRequest objetivo : objetivos) {
            if (objetivo.orden() == null || objetivo.orden() < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "El orden de los objetivos no puede ser negativo");
            }

            entidades.add(ObjetivoAprendizaje.builder()
                    .cursoId(cursoId)
                    .descripcion(objetivo.descripcion())
                    .orden(objetivo.orden())
                    .build());
        }
        objetivoRepository.saveAll(entidades);
    }

    private CursoResumenResponse construirResumen(Curso curso, CategoriaCurso categoria) {
        List<Leccion> lecciones = leccionRepository.findByCursoIdOrderByOrdenAsc(curso.getId());
        return CursoResumenResponse.builder()
                .id(curso.getId())
                .titulo(curso.getTitulo())
                .descripcion(curso.getDescripcion())
                .imagenUrl(curso.getImagenUrl())
                .categoria(toCategoriaResponse(categoria))
                .cantidadLecciones(lecciones.size())
                .duracionTotalSegundos(calcularDuracion(lecciones))
                .build();
    }

    private CursoDetalleResponse construirDetalle(Curso curso) {
        CategoriaCurso categoria = categoriaRepository.findById(curso.getCategoriaId()).orElse(null);
        List<Leccion> lecciones = leccionRepository.findByCursoIdOrderByOrdenAsc(curso.getId());

        List<LeccionResponse> leccionesResponse = lecciones.stream()
                .map(this::toLeccionResponse)
                .collect(Collectors.toList());

        List<ObjetivoAprendizajeResponse> objetivos = objetivoRepository
                .findByCursoIdOrderByOrdenAsc(curso.getId()).stream()
                .map(this::toObjetivoResponse)
                .collect(Collectors.toList());

        return CursoDetalleResponse.builder()
                .id(curso.getId())
                .titulo(curso.getTitulo())
                .descripcion(curso.getDescripcion())
                .imagenUrl(curso.getImagenUrl())
                .categoria(toCategoriaResponse(categoria))
                .objetivos(objetivos)
                .lecciones(leccionesResponse)
                .publicado(curso.getPublicado())
                .duracionTotalSegundos(calcularDuracion(lecciones))
                .build();
    }

    private long calcularDuracion(List<Leccion> lecciones) {
        return lecciones.stream()
                .map(Leccion::getDuracionSegundos)
                .filter(duracion -> duracion != null)
                .mapToLong(Integer::longValue)
                .sum();
    }

    private CategoriaCursoResponse toCategoriaResponse(CategoriaCurso categoria) {
        if (categoria == null) {
            return null;
        }
        return CategoriaCursoResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .build();
    }

    private LeccionResponse toLeccionResponse(Leccion leccion) {
        return LeccionResponse.builder()
                .id(leccion.getId())
                .titulo(leccion.getTitulo())
                .descripcion(leccion.getDescripcion())
                .orden(leccion.getOrden())
                .videoUrl(leccion.getVideoUrl())
                .duracionSegundos(leccion.getDuracionSegundos())
                .build();
    }

    private ObjetivoAprendizajeResponse toObjetivoResponse(ObjetivoAprendizaje objetivo) {
        return ObjetivoAprendizajeResponse.builder()
                .id(objetivo.getId())
                .descripcion(objetivo.getDescripcion())
                .orden(objetivo.getOrden())
                .build();
    }
}
