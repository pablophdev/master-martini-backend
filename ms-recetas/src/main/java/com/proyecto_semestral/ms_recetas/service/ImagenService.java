package com.proyecto_semestral.ms_recetas.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImagenService {

    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${app.imagenes.directorio:uploads/recetas}")
    private String directorio;

    @Value("${app.imagenes.url-base:/imagenes/recetas}")
    private String urlBase;

    public String guardar(MultipartFile archivo) {
        validarArchivo(archivo);

        String extension = obtenerExtension(archivo.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID() + extension;

        Path carpeta = obtenerCarpeta();
        try {
            Files.createDirectories(carpeta);
            try (InputStream contenido = archivo.getInputStream()) {
                Files.copy(contenido, carpeta.resolve(nombreArchivo), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar la imagen", e);
        }

        return urlBase + "/" + nombreArchivo;
    }

    public Resource cargar(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank() || nombreArchivo.contains("..")) {
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }

        Path archivo = obtenerCarpeta().resolve(nombreArchivo).normalize();
        if (!archivo.startsWith(obtenerCarpeta()) || !Files.exists(archivo)) {
            throw new IllegalArgumentException("La imagen no existe: " + nombreArchivo);
        }

        try {
            return new UrlResource(archivo.toUri());
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la imagen", e);
        }
    }

    public void eliminar(String url) {
        if (url == null || !url.startsWith(urlBase + "/")) {
            return;
        }

        String nombreArchivo = url.substring(urlBase.length() + 1);
        if (nombreArchivo.isBlank() || nombreArchivo.contains("..")) {
            return;
        }

        try {
            Files.deleteIfExists(obtenerCarpeta().resolve(nombreArchivo));
        } catch (IOException ignored) {
            // Si el archivo no se puede borrar, no se interrumpe la operación principal.
        }
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen es obligatorio");
        }

        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }

        obtenerExtension(archivo.getOriginalFilename());
    }

    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal == null) {
            throw new IllegalArgumentException("La imagen debe tener un nombre de archivo válido");
        }

        int punto = nombreOriginal.lastIndexOf('.');
        if (punto < 0) {
            throw new IllegalArgumentException("La imagen debe tener una extensión válida");
        }

        String extension = nombreOriginal.substring(punto).toLowerCase();
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new IllegalArgumentException(
                    "Formato no permitido. Use uno de: " + String.join(", ", EXTENSIONES_PERMITIDAS));
        }

        return extension;
    }

    private Path obtenerCarpeta() {
        return Paths.get(directorio).toAbsolutePath().normalize();
    }
}
