package com.proyecto_semestral.ms_auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.proyecto_semestral.ms_auth.dto.request.LoginRequest;
import com.proyecto_semestral.ms_auth.dto.request.RegistroRequest;
import com.proyecto_semestral.ms_auth.dto.response.AuthResponse;
import com.proyecto_semestral.ms_auth.entity.Rol;
import com.proyecto_semestral.ms_auth.entity.Usuario;
import com.proyecto_semestral.ms_auth.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(usuarioRepository, passwordEncoder, jwtService);
    }

    @Test
    void registrarNormalizaEmailHasheaPasswordYAsignaRolUser() {
        RegistroRequest request = new RegistroRequest(
                "  Ana Martini  ",
                "  ANA@Example.COM ",
                "password-seguro");
        when(usuarioRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password-seguro")).thenReturn("hash-bcrypt");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(7L);
            usuario.setCreadoEn(LocalDateTime.of(2026, 1, 2, 3, 4));
            return usuario;
        });
        when(jwtService.generarToken(any(Usuario.class))).thenReturn("jwt");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        AuthResponse response = authService.registrar(request);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario guardado = usuarioCaptor.getValue();
        assertEquals("Ana Martini", guardado.getNombre());
        assertEquals("ana@example.com", guardado.getEmail());
        assertEquals("hash-bcrypt", guardado.getPassword());
        assertEquals(Rol.USER, guardado.getRol());
        assertTrue(guardado.getActivo());
        assertEquals("jwt", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
        assertEquals(7L, response.usuario().id());
        assertEquals(Rol.USER, response.usuario().rol());
    }

    @Test
    void registrarRechazaEmailDuplicadoConConflict() {
        when(usuarioRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.registrar(
                        new RegistroRequest("Ana", "ANA@example.com", "password-seguro")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(usuarioRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void loginDevuelveTokenParaCredencialesValidas() {
        Usuario usuario = usuarioActivo();
        when(usuarioRepository.findByEmailIgnoreCase("ana@example.com"))
                .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password-seguro", "hash-bcrypt")).thenReturn(true);
        when(jwtService.generarToken(usuario)).thenReturn("jwt");
        when(jwtService.getExpirationSeconds()).thenReturn(900L);

        AuthResponse response = authService.login(
                new LoginRequest(" ANA@example.com ", "password-seguro"));

        assertEquals("jwt", response.accessToken());
        assertEquals(900L, response.expiresIn());
        assertEquals("ana@example.com", response.usuario().email());
    }

    @Test
    void loginNoRevelaSiElEmailNoExiste() {
        when(usuarioRepository.findByEmailIgnoreCase("nadie@example.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(
                        new LoginRequest("nadie@example.com", "password-incorrecto")));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Email o contraseña incorrectos", exception.getReason());
    }

    @Test
    void loginRechazaUsuarioInactivoAunqueElPasswordSeaCorrecto() {
        Usuario usuario = usuarioActivo();
        usuario.setActivo(false);
        when(usuarioRepository.findByEmailIgnoreCase("ana@example.com"))
                .thenReturn(Optional.of(usuario));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(
                        new LoginRequest("ana@example.com", "password-seguro")));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verify(passwordEncoder, never()).matches(any(), any());
        assertFalse(usuario.getActivo());
    }

    private Usuario usuarioActivo() {
        return Usuario.builder()
                .id(7L)
                .nombre("Ana")
                .email("ana@example.com")
                .password("hash-bcrypt")
                .rol(Rol.USER)
                .activo(true)
                .creadoEn(LocalDateTime.of(2026, 1, 2, 3, 4))
                .build();
    }
}
