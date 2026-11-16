package com.proyecto_semestral.ms_auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.proyecto_semestral.ms_auth.entity.Rol;
import com.proyecto_semestral.ms_auth.entity.Usuario;

class JwtServiceTest {

    private SecretKey key;

    @BeforeEach
    void setUp() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("HmacSHA256");
        generator.init(256);
        key = generator.generateKey();
    }

    @Test
    void generaJwtHs256ConIdentidadRolesYExpiracion() {
        JwtService jwtService = new JwtService(
                NimbusJwtEncoder.withSecretKey(key)
                        .algorithm(MacAlgorithm.HS256)
                        .build(),
                1200L);
        Usuario usuario = Usuario.builder()
                .id(42L)
                .nombre("Ana")
                .email("ana@example.com")
                .rol(Rol.ADMIN)
                .activo(true)
                .build();

        String token = jwtService.generarToken(usuario);

        Jwt jwt = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build()
                .decode(token);
        assertEquals("HS256", jwt.getHeaders().get("alg"));
        assertEquals("JWT", jwt.getHeaders().get("typ"));
        assertEquals("ms-auth", jwt.getClaimAsString("iss"));
        assertEquals("ana@example.com", jwt.getSubject());
        assertEquals(42L, ((Number) jwt.getClaim("userId")).longValue());
        assertEquals("Ana", jwt.getClaimAsString("nombre"));
        assertEquals(List.of("ADMIN"), jwt.getClaimAsStringList("roles"));
        assertEquals(1200L, Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()).toSeconds());
        assertTrue(jwt.getExpiresAt().isAfter(jwt.getIssuedAt()));
    }

    @Test
    void rechazaExpiracionNoPositivaAlConfigurarServicio() {
        NimbusJwtEncoder encoder = NimbusJwtEncoder.withSecretKey(key)
                .algorithm(MacAlgorithm.HS256)
                .build();

        assertThrows(IllegalArgumentException.class, () -> new JwtService(encoder, 0));
        assertThrows(IllegalArgumentException.class, () -> new JwtService(encoder, -1));
    }
}
