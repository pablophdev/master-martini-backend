package com.proyecto_semestral.ms_auth.config;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto_semestral.ms_auth.entity.Rol;
import com.proyecto_semestral.ms_auth.entity.Usuario;
import com.proyecto_semestral.ms_auth.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.admin.nombre}")
    private String nombre;

    @Value("${auth.admin.email}")
    private String email;

    @Value("${auth.admin.password}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return;
        }
        if (password.length() < 8 || password.length() > 72) {
            throw new IllegalStateException("AUTH_ADMIN_PASSWORD debe tener entre 8 y 72 caracteres");
        }

        String emailNormalizado = email.trim().toLowerCase(Locale.ROOT);
        Usuario admin = usuarioRepository.findByEmailIgnoreCase(emailNormalizado)
                .orElseGet(() -> Usuario.builder()
                        .nombre(nombre.trim())
                        .email(emailNormalizado)
                        .password(passwordEncoder.encode(password))
                        .activo(true)
                        .build());

        admin.setRol(Rol.ADMIN);
        admin.setActivo(true);
        usuarioRepository.save(admin);
    }
}
