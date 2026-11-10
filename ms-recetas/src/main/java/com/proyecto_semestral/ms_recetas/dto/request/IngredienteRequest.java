package com.proyecto_semestral.ms_recetas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
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
public class IngredienteRequest {

    @NotBlank(message = "El nombre del ingrediente es obligatorio")
    @Size(max = 150, message = "El nombre del ingrediente no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 100, message = "La cantidad no puede superar los 100 caracteres")
    private String cantidadTexto;

    @PositiveOrZero(message = "El orden no puede ser negativo")
    private Integer orden;
}
