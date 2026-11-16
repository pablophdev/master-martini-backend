package com.proyecto_semestral.ms_recetas.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.proyecto_semestral.ms_recetas.entity.ImagenReceta;
import com.proyecto_semestral.ms_recetas.entity.Receta;
import com.proyecto_semestral.ms_recetas.repository.ImagenRecetaRepository;
import com.proyecto_semestral.ms_recetas.repository.RecetaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Migra a la base de datos las imágenes que versiones anteriores guardaban
 * como archivos en disco y referenciaban con un nombre de archivo en la URL.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MigracionImagenesAntiguas implements ApplicationRunner {

    private static final String PREFIJO = "/imagenes/recetas/";

    private final RecetaRepository recetaRepository;
    private final ImagenRecetaRepository imagenRepository;

    @Value("${app.uploads.recetas:uploads/recetas}")
    private String directorioImagenes;

    @Override
    public void run(ApplicationArguments args) {
        Path base = Path.of(directorioImagenes).toAbsolutePath().normalize();
        if (!Files.isDirectory(base)) {
            return;
        }

        for (Receta receta : recetaRepository.findAll()) {
            String url = receta.getImagenUrl();
            if (url == null || !url.startsWith(PREFIJO)) {
                continue;
            }

            String nombre = url.substring(PREFIJO.length());
            if (nombre.isBlank() || nombre.matches("\\d+")) {
                continue;
            }

            Path archivo = base.resolve(nombre).normalize();
            if (!archivo.startsWith(base) || !Files.isRegularFile(archivo)) {
                continue;
            }

            try {
                ImagenReceta imagen = imagenRepository.findByRecetaId(receta.getId())
                        .orElseGet(() -> ImagenReceta.builder().recetaId(receta.getId()).build());
                imagen.setNombreArchivo(nombre);
                imagen.setTipoContenido(tipoContenido(nombre));
                imagen.setDatos(Files.readAllBytes(archivo));
                imagenRepository.save(imagen);

                receta.setImagenUrl(PREFIJO + receta.getId());
                recetaRepository.save(receta);
                log.info("Imagen antigua migrada a la base de datos para la receta {}", receta.getId());
            } catch (IOException exception) {
                log.warn("No se pudo migrar la imagen {} de la receta {}", nombre, receta.getId(), exception);
            }
        }
    }

    private String tipoContenido(String nombre) {
        String minuscula = nombre.toLowerCase();
        if (minuscula.endsWith(".png")) {
            return "image/png";
        }
        if (minuscula.endsWith(".gif")) {
            return "image/gif";
        }
        if (minuscula.endsWith(".webp")) {
            return "image/webp";
        }
        if (minuscula.endsWith(".avif")) {
            return "image/avif";
        }
        if (minuscula.endsWith(".heic")) {
            return "image/heic";
        }
        return "image/jpeg";
    }
}
