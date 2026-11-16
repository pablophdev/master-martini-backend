package com.proyecto_semestral.ms_auth.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UsuarioResponse usuario) {
}
