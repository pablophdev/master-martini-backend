package com.proyecto_semestral.ms_auth.dto.response;

import java.time.LocalDateTime;

import com.proyecto_semestral.ms_auth.entity.Rol;

import lombok.Builder;

@Builder
public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        Rol rol,
        Boolean activo,
        String nombreNegocio,
        String tipoNegocio,
        String tamanoNegocio,
        String fotoPerfilUrl,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn) {
}
