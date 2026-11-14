package com.proyecto_semestral.ms_contacto.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ConsultaResponse(
        Long id,
        Long usuarioId,
        String email,
        String telefono,
        String nombreNegocio,
        String tipoNegocio,
        String tamanoNegocio,
        String descripcion,
        String estado,
        LocalDateTime creadoEn) {
}
