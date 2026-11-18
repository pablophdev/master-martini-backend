package com.proyecto_semestral.ms_auth.service;

import java.io.IOException;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_semestral.ms_auth.entity.ImagenPerfil;
import com.proyecto_semestral.ms_auth.repository.ImagenPerfilRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImagenPerfilService {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/avif", "image/heic");

    private final ImagenPerfilRepository imagenRepository;

    @Transactional
    public String guardar(Long usuarioId, MultipartFile archivo) {
        validar(archivo);
        ImagenPerfil imagen = imagenRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> ImagenPerfil.builder().usuarioId(usuarioId).build());
        try {
            imagen.setNombreArchivo(nombreSeguro(archivo.getOriginalFilename()));
            imagen.setTipoContenido(archivo.getContentType());
            imagen.setDatos(archivo.getBytes());
            imagenRepository.save(imagen);
            return "/imagenes/perfiles/" + usuarioId;
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo leer la imagen", exception);
        }
    }

    @Transactional(readOnly = true)
    public ImagenPerfil cargar(Long usuarioId) {
        return imagenRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Imagen de perfil no encontrada"));
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
            return "perfil";
        }
        return nombre.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
