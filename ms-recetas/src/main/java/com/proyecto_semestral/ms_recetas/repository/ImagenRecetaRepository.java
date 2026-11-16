package com.proyecto_semestral.ms_recetas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_recetas.entity.ImagenReceta;

@Repository
public interface ImagenRecetaRepository extends JpaRepository<ImagenReceta, Long> {

    Optional<ImagenReceta> findByRecetaId(Long recetaId);
}
