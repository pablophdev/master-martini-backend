package com.proyecto_semestral.ms_auth.controller;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.proyecto_semestral.ms_auth.dto.request.ActualizarPerfilRequest;
import com.proyecto_semestral.ms_auth.dto.request.LoginRequest;
import com.proyecto_semestral.ms_auth.dto.request.RegistroRequest;
import com.proyecto_semestral.ms_auth.dto.response.AuthResponse;
import com.proyecto_semestral.ms_auth.dto.response.UsuarioResponse;
import com.proyecto_semestral.ms_auth.service.AuthService;
import com.proyecto_semestral.ms_auth.service.ImagenPerfilService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ImagenPerfilService imagenPerfilService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity
                .created(URI.create("/api/auth/me"))
                .body(response);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UsuarioResponse obtenerUsuarioActual(@AuthenticationPrincipal Jwt jwt) {
        return authService.obtenerPorEmail(jwt.getSubject());
    }

    @PutMapping("/perfil")
    public UsuarioResponse actualizarPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ActualizarPerfilRequest request) {
        return authService.actualizarPerfil(jwt.getSubject(), request);
    }

    @PostMapping(
            value = "/perfil/foto",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UsuarioResponse subirFotoPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("image") MultipartFile imagen) {
        UsuarioResponse actual = authService.obtenerPorEmail(jwt.getSubject());
        String url = imagenPerfilService.guardar(actual.id(), imagen);
        return authService.actualizarFotoPerfil(jwt.getSubject(), url);
    }
}
