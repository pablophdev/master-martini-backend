package com.proyecto_semestral.ms_cursos.service;

import java.io.IOException;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_semestral.ms_cursos.entity.ImagenCurso;
import com.proyecto_semestral.ms_cursos.repository.ImagenCursoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImagenCursoService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/avif", "image/heic");

    private final ImagenCursoRepository imagenRepository;

    @Transactional
    public String guardar(Long cursoId, MultipartFile archivo) {
        validar(archivo);
        ImagenCurso imagen = imagenRepository.findByCursoId(cursoId)
                .orElseGet(() -> ImagenCurso.builder().cursoId(cursoId).build());
        try {
            imagen.setNombreArchivo(nombreSeguro(archivo.getOriginalFilename()));
            imagen.setTipoContenido(archivo.getContentType());
            imagen.setDatos(archivo.getBytes());
            imagenRepository.save(imagen);
            return "/imagenes/cursos/" + cursoId;
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer la imagen", exception);
        }
    }

    @Transactional(readOnly = true)
    public ImagenCurso cargar(Long cursoId) {
        return imagenRepository.findByCursoId(cursoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Imagen de curso no encontrada"));
    }

    private void validar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen es obligatorio");
        }
        if (archivo.getContentType() == null || !TIPOS_PERMITIDOS.contains(archivo.getContentType())) {
            throw new IllegalArgumentException("El archivo debe ser una imagen válida");
        }
    }

    private String nombreSeguro(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "imagen";
        }
        return nombre.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
