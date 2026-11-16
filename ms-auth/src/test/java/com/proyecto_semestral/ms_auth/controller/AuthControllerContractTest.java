package com.proyecto_semestral.ms_auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_semestral.ms_auth.dto.response.AuthResponse;
import com.proyecto_semestral.ms_auth.dto.response.UsuarioResponse;
import com.proyecto_semestral.ms_auth.entity.Rol;
import com.proyecto_semestral.ms_auth.service.AuthService;

class AuthControllerContractTest {

    private AuthService authService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(authService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void registerResponde201LocationYContratoAuth() throws Exception {
        when(authService.registrar(any())).thenReturn(authResponse());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Ana",
                                  "email": "ana@example.com",
                                  "password": "password-seguro"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/auth/me"))
                .andExpect(jsonPath("$.accessToken").value("jwt"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.usuario.id").value(7))
                .andExpect(jsonPath("$.usuario.nombre").value("Ana"))
                .andExpect(jsonPath("$.usuario.email").value("ana@example.com"))
                .andExpect(jsonPath("$.usuario.rol").value("USER"))
                .andExpect(jsonPath("$.usuario.activo").value(true));
    }

    @Test
    void loginResponde200ConElMismoContratoAuth() throws Exception {
        when(authService.login(any())).thenReturn(authResponse());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ana@example.com",
                                  "password": "password-seguro"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt"))
                .andExpect(jsonPath("$.usuario.email").value("ana@example.com"));
    }

    @Test
    void validacionResponde400ConErroresPorCampo() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "email": "no-es-email",
                                  "password": "corta"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("La solicitud contiene campos inválidos"))
                .andExpect(jsonPath("$.path").value("/api/auth/register"))
                .andExpect(jsonPath("$.fields.nombre").exists())
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.password").exists());
    }

    @Test
    void credencialesInvalidasResponden401SinFiltrarDetalles() throws Exception {
        when(authService.login(any())).thenThrow(new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Email o contraseña incorrectos"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "ana@example.com",
                                  "password": "password-incorrecto"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Email o contraseña incorrectos"))
                .andExpect(jsonPath("$.path").value("/api/auth/login"));
    }

    @Test
    void meUsaElSubjectDelJwtYDevuelveElContratoUsuario() {
        UsuarioResponse usuario = authResponse().usuario();
        when(authService.obtenerPorEmail("ana@example.com")).thenReturn(usuario);
        Jwt jwt = Jwt.withTokenValue("jwt")
                .header("alg", "HS256")
                .subject("ana@example.com")
                .claim("roles", java.util.List.of("USER"))
                .build();

        UsuarioResponse response = new AuthController(authService).obtenerUsuarioActual(jwt);

        assertEquals(usuario, response);
    }

    private AuthResponse authResponse() {
        UsuarioResponse usuario = new UsuarioResponse(
                7L,
                "Ana",
                "ana@example.com",
                Rol.USER,
                true,
                LocalDateTime.of(2026, 1, 2, 3, 4));
        return new AuthResponse("jwt", "Bearer", 3600L, usuario);
    }
}
