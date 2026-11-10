package com.proyecto_semestral.ms_recetas.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearRecetaRequest {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String titulo;

    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    @Positive(message = "El id de la categoría debe ser mayor que 0")
    private Long categoriaId;

    @Size(max = 500, message = "La URL del video no puede superar los 500 caracteres")
    private String videoUrl;

    @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
    private String imagenUrl;

    @Size(max = 100, message = "La duración no puede superar los 100 caracteres")
    private String duracionTexto;

    @Size(max = 100, message = "El rendimiento no puede superar los 100 caracteres")
    private String rendimientoTexto;

    @Size(max = 150, message = "El título de insumos no puede superar los 150 caracteres")
    private String insumosTitulo;

    @Size(max = 2000, message = "La descripción de insumos no puede superar los 2000 caracteres")
    private String insumosDescripcion;

    private Boolean publicada;

    @NotBlank(message = "El usuario creador es obligatorio")
    @Size(max = 100, message = "El usuario creador no puede superar los 100 caracteres")
    private String creadoPor;

    @Valid
    @Size(max = 100, message = "No se pueden enviar más de 100 ingredientes")
    private List<IngredienteRequest> ingredientes;

    @Valid
    @Size(max = 100, message = "No se pueden enviar más de 100 pasos")
    private List<PasoRequest> pasos;
}
