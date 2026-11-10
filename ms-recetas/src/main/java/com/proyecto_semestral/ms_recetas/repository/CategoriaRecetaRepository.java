package com.proyecto_semestral.ms_recetas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_recetas.entity.CategoriaReceta;

@Repository
public interface CategoriaRecetaRepository extends JpaRepository<CategoriaReceta, Long> {

    Optional<CategoriaReceta> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
