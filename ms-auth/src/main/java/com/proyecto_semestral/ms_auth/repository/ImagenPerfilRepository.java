package com.proyecto_semestral.ms_auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_auth.entity.ImagenPerfil;

@Repository
public interface ImagenPerfilRepository extends JpaRepository<ImagenPerfil, Long> {

    Optional<ImagenPerfil> findByUsuarioId(Long usuarioId);
}
