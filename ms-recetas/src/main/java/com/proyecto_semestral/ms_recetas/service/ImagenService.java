package com.proyecto_semestral.ms_recetas.service;

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
public class ImagenService {

    private static final Set<String> EXTENSIONES_PERMITIDAS =
            Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final String UUID_REGEX =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-"
                    + "[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

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
        validarNombreGenerado(nombreArchivo);

        Path carpeta = obtenerCarpeta();
        Path archivo = carpeta.resolve(nombreArchivo).normalize();
        if (!archivo.startsWith(carpeta)
                || Files.isSymbolicLink(archivo)
                || !Files.isRegularFile(archivo, LinkOption.NOFOLLOW_LINKS)
                || !Files.isReadable(archivo)) {
            throw imagenNoEncontrada();
        }

        try {
            Resource recurso = new UrlResource(archivo.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw imagenNoEncontrada();
            }
            return recurso;
        } catch (IOException e) {
            throw imagenNoEncontrada();
        }
    }

    public MediaType obtenerTipoContenido(String nombreArchivo) {
        validarNombreGenerado(nombreArchivo);
        String extension = obtenerExtension(nombreArchivo);
        return switch (extension) {
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

        String nombreArchivo = url.substring(urlBase.length() + 1);
        try {
            validarNombreGenerado(nombreArchivo);
        } catch (ResponseStatusException exception) {
            return;
        }

        try {
            Path carpeta = obtenerCarpeta();
            Path archivo = carpeta.resolve(nombreArchivo).normalize();
            if (archivo.startsWith(carpeta)) {
                Files.deleteIfExists(archivo);
            }
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

    private void validarNombreGenerado(String nombreArchivo) {
        if (nombreArchivo == null
                || nombreArchivo.isBlank()
                || nombreArchivo.contains("/")
                || nombreArchivo.contains("\\")) {
            throw imagenNoEncontrada();
        }

        int punto = nombreArchivo.lastIndexOf('.');
        if (punto <= 0) {
            throw imagenNoEncontrada();
        }

        try {
            String identificador = nombreArchivo.substring(0, punto);
            if (!identificador.matches(UUID_REGEX)) {
                throw new IllegalArgumentException("UUID inválido");
            }
            UUID.fromString(identificador);
            obtenerExtension(nombreArchivo);
        } catch (IllegalArgumentException exception) {
            throw imagenNoEncontrada();
        }
    }

    private ResponseStatusException imagenNoEncontrada() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Imagen no encontrada");
    }

    private Path obtenerCarpeta() {
        return Paths.get(directorio).toAbsolutePath().normalize();
    }
}
