package com.proyecto_semestral.ms_recetas.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_semestral.ms_recetas.dto.request.ActualizarRecetaRequest;
import com.proyecto_semestral.ms_recetas.dto.request.CrearRecetaRequest;
import com.proyecto_semestral.ms_recetas.dto.request.IngredienteRequest;
import com.proyecto_semestral.ms_recetas.dto.request.PasoRequest;
import com.proyecto_semestral.ms_recetas.dto.response.CategoriaRecetaResponse;
import com.proyecto_semestral.ms_recetas.dto.response.IngredienteRecetaResponse;
import com.proyecto_semestral.ms_recetas.dto.response.PasoPreparacionResponse;
import com.proyecto_semestral.ms_recetas.dto.response.RecetaDetalleResponse;
import com.proyecto_semestral.ms_recetas.dto.response.RecetaResumenResponse;
import com.proyecto_semestral.ms_recetas.entity.CategoriaReceta;
import com.proyecto_semestral.ms_recetas.entity.IngredienteReceta;
import com.proyecto_semestral.ms_recetas.entity.PasoPreparacion;
import com.proyecto_semestral.ms_recetas.entity.Receta;
import com.proyecto_semestral.ms_recetas.repository.CategoriaRecetaRepository;
import com.proyecto_semestral.ms_recetas.repository.IngredienteRecetaRepository;
import com.proyecto_semestral.ms_recetas.repository.PasoPreparacionRepository;
import com.proyecto_semestral.ms_recetas.repository.RecetaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecetaService {

    private static final int TAMANO_PAGINA = 12;

    private final RecetaRepository recetaRepository;
    private final CategoriaRecetaRepository categoriaRepository;
    private final IngredienteRecetaRepository ingredienteRepository;
    private final PasoPreparacionRepository pasoRepository;

    @Transactional(readOnly = true)
    public List<RecetaResumenResponse> listar(String busqueda, Long categoriaId) {
        List<Receta> recetas = buscarRecetas(busqueda, categoriaId);

        Map<Long, CategoriaReceta> categorias = categoriaRepository.findAll().stream()
                .collect(Collectors.toMap(CategoriaReceta::getId, Function.identity()));

        return recetas.stream()
                .map(receta -> toResumen(receta, categorias.get(receta.getCategoriaId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<RecetaResumenResponse> listar(String busqueda, Long categoriaId, int pagina) {
        if (pagina < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La página no puede ser negativa");
        }

        List<RecetaResumenResponse> recetas = listar(busqueda, categoriaId);
        int desde = Math.min(pagina * TAMANO_PAGINA, recetas.size());
        int hasta = Math.min(desde + TAMANO_PAGINA, recetas.size());

        return new PageImpl<>(
                recetas.subList(desde, hasta),
                PageRequest.of(pagina, TAMANO_PAGINA),
                recetas.size());
    }

    @Transactional(readOnly = true)
    public List<RecetaResumenResponse> listarTodas() {
        Map<Long, CategoriaReceta> categorias = categoriaRepository.findAll().stream()
                .collect(Collectors.toMap(CategoriaReceta::getId, Function.identity()));

        return recetaRepository.findAll().stream()
                .map(receta -> toResumen(receta, categorias.get(receta.getCategoriaId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecetaDetalleResponse obtener(Long id) {
        return toDetalle(buscarReceta(id));
    }

    @Transactional(readOnly = true)
    public RecetaDetalleResponse obtenerPublicada(Long id) {
        Receta receta = recetaRepository.findByIdAndPublicadaTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Receta publicada no encontrada con id " + id));
        return toDetalle(receta);
    }

    @Transactional(readOnly = true)
    public List<CategoriaRecetaResponse> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(categoria -> CategoriaRecetaResponse.builder()
                        .id(categoria.getId())
                        .nombre(categoria.getNombre())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public RecetaDetalleResponse crear(CrearRecetaRequest request) {
        buscarCategoria(request.getCategoriaId());

        Receta receta = Receta.builder()
                .titulo(request.getTitulo())
                .descripcion(request.getDescripcion())
                .categoriaId(request.getCategoriaId())
                .videoUrl(request.getVideoUrl())
                .imagenUrl(request.getImagenUrl())
                .duracionTexto(request.getDuracionTexto())
                .rendimientoTexto(request.getRendimientoTexto())
                .insumosTitulo(request.getInsumosTitulo())
                .insumosDescripcion(request.getInsumosDescripcion())
                .publicada(Boolean.TRUE.equals(request.getPublicada()))
                .creadoPor(request.getCreadoPor())
                .build();

        Receta guardada = recetaRepository.save(receta);

        guardarIngredientes(guardada.getId(), request.getIngredientes());
        guardarPasos(guardada.getId(), request.getPasos());

        return toDetalle(guardada);
    }

    @Transactional
    public RecetaDetalleResponse actualizar(Long id, ActualizarRecetaRequest request) {
        Receta receta = buscarReceta(id);

        if (request.getCategoriaId() != null) {
            buscarCategoria(request.getCategoriaId());
            receta.setCategoriaId(request.getCategoriaId());
        }
        if (request.getTitulo() != null) {
            receta.setTitulo(request.getTitulo());
        }
        if (request.getDescripcion() != null) {
            receta.setDescripcion(request.getDescripcion());
        }
        if (request.getVideoUrl() != null) {
            receta.setVideoUrl(request.getVideoUrl());
        }
        if (request.getImagenUrl() != null) {
            receta.setImagenUrl(request.getImagenUrl());
        }
        if (request.getDuracionTexto() != null) {
            receta.setDuracionTexto(request.getDuracionTexto());
        }
        if (request.getRendimientoTexto() != null) {
            receta.setRendimientoTexto(request.getRendimientoTexto());
        }
        if (request.getInsumosTitulo() != null) {
            receta.setInsumosTitulo(request.getInsumosTitulo());
        }
        if (request.getInsumosDescripcion() != null) {
            receta.setInsumosDescripcion(request.getInsumosDescripcion());
        }
        if (request.getPublicada() != null) {
            receta.setPublicada(request.getPublicada());
        }

        Receta guardada = recetaRepository.save(receta);

        if (request.getIngredientes() != null) {
            ingredienteRepository.deleteByRecetaId(id);
            guardarIngredientes(id, request.getIngredientes());
        }
        if (request.getPasos() != null) {
            pasoRepository.deleteByRecetaId(id);
            guardarPasos(id, request.getPasos());
        }

        return toDetalle(guardada);
    }

    @Transactional
    public void eliminar(Long id) {
        Receta receta = buscarReceta(id);

        ingredienteRepository.deleteByRecetaId(id);
        pasoRepository.deleteByRecetaId(id);
        recetaRepository.delete(receta);
    }

    @Transactional
    public RecetaDetalleResponse actualizarImagen(Long id, String imagenUrl) {
        Receta receta = buscarReceta(id);
        receta.setImagenUrl(imagenUrl);
        return toDetalle(recetaRepository.save(receta));
    }

    private List<Receta> buscarRecetas(String busqueda, Long categoriaId) {
        boolean hayBusqueda = busqueda != null && !busqueda.isBlank();

        if (hayBusqueda && categoriaId != null) {
            return recetaRepository
                    .findByTituloContainingIgnoreCaseAndCategoriaIdAndPublicadaTrue(busqueda, categoriaId);
        }
        if (hayBusqueda) {
            return recetaRepository.findByTituloContainingIgnoreCaseAndPublicadaTrue(busqueda);
        }
        if (categoriaId != null) {
            return recetaRepository.findByCategoriaIdAndPublicadaTrue(categoriaId);
        }
        return recetaRepository.findByPublicadaTrue();
    }

    private Receta buscarReceta(Long id) {
        return recetaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Receta no encontrada con id " + id));
    }

    private CategoriaReceta buscarCategoria(Long categoriaId) {
        return categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "CategorÃ­a no encontrada con id " + categoriaId));
    }

    private void guardarIngredientes(Long recetaId, List<IngredienteRequest> ingredientes) {
        if (ingredientes == null || ingredientes.isEmpty()) {
            return;
        }

        List<IngredienteReceta> entidades = new ArrayList<>();
        int posicion = 0;
        for (IngredienteRequest ingrediente : ingredientes) {
            entidades.add(IngredienteReceta.builder()
                    .recetaId(recetaId)
                    .nombre(ingrediente.getNombre())
                    .cantidadTexto(ingrediente.getCantidadTexto())
                    .orden(ingrediente.getOrden() != null ? ingrediente.getOrden() : posicion)
                    .build());
            posicion++;
        }

        ingredienteRepository.saveAll(entidades);
    }

    private void guardarPasos(Long recetaId, List<PasoRequest> pasos) {
        if (pasos == null || pasos.isEmpty()) {
            return;
        }

        List<PasoPreparacion> entidades = new ArrayList<>();
        for (PasoRequest paso : pasos) {
            entidades.add(PasoPreparacion.builder()
                    .recetaId(recetaId)
                    .numero(paso.getNumero())
                    .descripcion(paso.getDescripcion())
                    .build());
        }

        pasoRepository.saveAll(entidades);
    }

    private RecetaResumenResponse toResumen(Receta receta, CategoriaReceta categoria) {
        return RecetaResumenResponse.builder()
                .id(receta.getId())
                .titulo(receta.getTitulo())
                .descripcion(receta.getDescripcion())
                .imagenUrl(receta.getImagenUrl())
                .duracionTexto(receta.getDuracionTexto())
                .categoria(toCategoriaResponse(categoria))
                .build();
    }

    private RecetaDetalleResponse toDetalle(Receta receta) {
        CategoriaReceta categoria = categoriaRepository.findById(receta.getCategoriaId()).orElse(null);

        List<IngredienteRecetaResponse> ingredientes = ingredienteRepository
                .findByRecetaIdOrderByOrdenAsc(receta.getId()).stream()
                .map(ingrediente -> IngredienteRecetaResponse.builder()
                        .id(ingrediente.getId())
                        .nombre(ingrediente.getNombre())
                        .cantidadTexto(ingrediente.getCantidadTexto())
                        .orden(ingrediente.getOrden())
                        .build())
                .collect(Collectors.toList());

        List<PasoPreparacionResponse> pasos = pasoRepository
                .findByRecetaIdOrderByNumeroAsc(receta.getId()).stream()
                .map(paso -> PasoPreparacionResponse.builder()
                        .id(paso.getId())
                        .numero(paso.getNumero())
                        .descripcion(paso.getDescripcion())
                        .build())
                .collect(Collectors.toList());

        return RecetaDetalleResponse.builder()
                .id(receta.getId())
                .titulo(receta.getTitulo())
                .descripcion(receta.getDescripcion())
                .imagenUrl(receta.getImagenUrl())
                .duracionTexto(receta.getDuracionTexto())
                .categoria(toCategoriaResponse(categoria))
                .videoUrl(receta.getVideoUrl())
                .rendimientoTexto(receta.getRendimientoTexto())
                .insumosTitulo(receta.getInsumosTitulo())
                .insumosDescripcion(receta.getInsumosDescripcion())
                .ingredientes(ingredientes)
                .pasos(pasos)
                .build();
    }

    private CategoriaRecetaResponse toCategoriaResponse(CategoriaReceta categoria) {
        if (categoria == null) {
            return null;
        }
        return CategoriaRecetaResponse.builder()
                .id(categoria.getId())
                .nombre(categoria.getNombre())
                .build();
    }
}

