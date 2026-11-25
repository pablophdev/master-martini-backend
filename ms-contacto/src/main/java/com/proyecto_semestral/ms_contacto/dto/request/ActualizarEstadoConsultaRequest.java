package com.proyecto_semestral.ms_contacto.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ActualizarEstadoConsultaRequest(
        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 30, message = "El estado no puede superar los 30 caracteres")
        @Pattern(
                regexp = "PENDIENTE|EN_PROCESO|ATENDIDA",
                message = "El estado debe ser PENDIENTE, EN_PROCESO o ATENDIDA")
        @Schema(example = "PENDIENTE", allowableValues = {"PENDIENTE", "EN_PROCESO", "ATENDIDA"})
        String estado) {
}
