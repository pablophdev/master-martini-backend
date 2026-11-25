package com.proyecto_semestral.ms_auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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

@Tag(name = "Autenticación y perfil")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ImagenPerfilService imagenPerfilService;

    @Operation(summary = "Registrar un usuario y emitir un token JWT", description = "Público; no requiere token.")
    @ApiResponse(responseCode = "201", description = "Creado correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.proyecto_semestral.ms_auth.dto.response.ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflicto con un registro existente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.proyecto_semestral.ms_auth.dto.response.ApiErrorResponse.class)))
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        AuthResponse response = authService.registrar(request);
        return ResponseEntity
                .created(URI.create("/api/auth/me"))
                .body(response);
    }

    @Operation(summary = "Iniciar sesión y emitir un token JWT", description = "Público; no requiere token.")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.proyecto_semestral.ms_auth.dto.response.ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Consultar el usuario autenticado", description = "Requiere un JWT de usuario autenticado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.proyecto_semestral.ms_auth.dto.response.ApiErrorResponse.class)))
    @GetMapping("/me")
    public UsuarioResponse obtenerUsuarioActual(@Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt) {
        return authService.obtenerPorEmail(jwt.getSubject());
    }

    @Operation(summary = "Actualizar el perfil del usuario autenticado", description = "Requiere un JWT de usuario autenticado.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.proyecto_semestral.ms_auth.dto.response.ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.proyecto_semestral.ms_auth.dto.response.ApiErrorResponse.class)))
    @PutMapping("/perfil")
    public UsuarioResponse actualizarPerfil(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ActualizarPerfilRequest request) {
        return authService.actualizarPerfil(jwt.getSubject(), request);
    }

    @Operation(summary = "Subir o reemplazar la foto del usuario autenticado", description = "Requiere un JWT de usuario autenticado. Enviar el archivo en el campo image (multipart/form-data). JPEG, PNG, GIF, WebP, AVIF o HEIC. Límite: 10 MB.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "200", description = "Operación realizada correctamente", useReturnTypeSchema = true)
    @ApiResponse(responseCode = "400", description = "Solicitud o parámetros inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.proyecto_semestral.ms_auth.dto.response.ApiErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "Token ausente o inválido, o credenciales incorrectas", content = @Content)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.proyecto_semestral.ms_auth.dto.response.ApiErrorResponse.class)))
    @ApiResponse(responseCode = "413", description = "El archivo supera el límite configurado", content = @Content)
    @PostMapping(
            value = "/perfil/foto",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UsuarioResponse subirFotoPerfil(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @RequestParam("image") MultipartFile imagen) {
        UsuarioResponse actual = authService.obtenerPorEmail(jwt.getSubject());
        String url = imagenPerfilService.guardar(actual.id(), imagen);
        return authService.actualizarFotoPerfil(jwt.getSubject(), url);
    }
}
