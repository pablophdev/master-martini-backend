package com.proyecto_semestral.ms_contacto.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CrearConsultaRequest(
        @Positive(message = "El id del usuario debe ser mayor que cero")
        Long usuarioId,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 254, message = "El email no puede superar los 254 caracteres")
        String email,

        @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
        String telefono,

        @NotBlank(message = "El nombre del negocio es obligatorio")
        @Size(max = 150, message = "El nombre del negocio no puede superar los 150 caracteres")
        String nombreNegocio,

        @Size(max = 100, message = "El tipo de negocio no puede superar los 100 caracteres")
        String tipoNegocio,

        @Size(max = 50, message = "El tamaño del negocio no puede superar los 50 caracteres")
        String tamanoNegocio,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 5000, message = "La descripción no puede superar los 5000 caracteres")
        String descripcion) {
}
