package com.proyecto_semestral.ms_recetas.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PasoRequest {

    @NotNull(message = "El número del paso es obligatorio")
    @Min(value = 1, message = "El número del paso debe ser mayor o igual a 1")
    private Integer numero;

    @NotBlank(message = "La descripción del paso es obligatoria")
    @Size(max = 2000, message = "La descripción del paso no puede superar los 2000 caracteres")
    private String descripcion;
}
