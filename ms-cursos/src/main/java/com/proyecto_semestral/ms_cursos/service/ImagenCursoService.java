package com.proyecto_semestral.ms_cursos.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImagenCursoService {

    private static final Set<String> EXTENSIONES =
            Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${app.imagenes.directorio:uploads/cursos}")
    private String directorio;

    @Value("${app.imagenes.url-base:/imagenes/cursos}")
    private String urlBase;

    public String guardar(MultipartFile archivo) {
        validar(archivo);
        String extension = extension(archivo.getOriginalFilename());
        String nombre = UUID.randomUUID() + extension;
        Path carpeta = carpeta();
        try {
            Files.createDirectories(carpeta);
            try (InputStream contenido = archivo.getInputStream()) {
                Files.copy(contenido, carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("No se pudo guardar la imagen del curso", exception);
        }
        return urlBase + "/" + nombre;
    }

    public Resource cargar(String nombre) {
        validarNombre(nombre);
        Path archivo = carpeta().resolve(nombre).normalize();
        if (!archivo.startsWith(carpeta())
                || Files.isSymbolicLink(archivo)
                || !Files.isRegularFile(archivo, LinkOption.NOFOLLOW_LINKS)
                || !Files.isReadable(archivo)) {
            throw noEncontrada();
        }
        try {
            Resource recurso = new UrlResource(archivo.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw noEncontrada();
            }
            return recurso;
        } catch (IOException exception) {
            throw noEncontrada();
        }
    }

    public MediaType tipoContenido(String nombre) {
        return switch (extension(nombre)) {
            case ".jpg", ".jpeg" -> MediaType.IMAGE_JPEG;
            case ".png" -> MediaType.IMAGE_PNG;
            case ".gif" -> MediaType.IMAGE_GIF;
            case ".webp" -> MediaType.parseMediaType("image/webp");
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    public void eliminar(String url) {
        if (url == null || !url.startsWith(urlBase + "/")) {
            return;
        }
        String nombre = url.substring(urlBase.length() + 1);
        try {
            validarNombre(nombre);
            Files.deleteIfExists(carpeta().resolve(nombre).normalize());
        } catch (IOException | ResponseStatusException ignored) {
            // La imagen anterior no impide guardar la nueva.
        }
    }

    private void validar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen es obligatorio");
        }
        if (archivo.getContentType() == null || !archivo.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }
        extension(archivo.getOriginalFilename());
    }

    private String extension(String nombre) {
        if (nombre == null || nombre.lastIndexOf('.') < 0) {
            throw new IllegalArgumentException("La imagen debe tener una extensión válida");
        }
        String extension = nombre.substring(nombre.lastIndexOf('.')).toLowerCase();
        if (!EXTENSIONES.contains(extension)) {
            throw new IllegalArgumentException("Formato de imagen no permitido");
        }
        return extension;
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.contains("/") || nombre.contains("\\")) {
            throw noEncontrada();
        }
        int punto = nombre.lastIndexOf('.');
        try {
            UUID.fromString(nombre.substring(0, punto));
            extension(nombre);
        } catch (RuntimeException exception) {
            throw noEncontrada();
        }
    }

    private Path carpeta() {
        return Paths.get(directorio).toAbsolutePath().normalize();
    }

    private ResponseStatusException noEncontrada() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada");
    }
}
