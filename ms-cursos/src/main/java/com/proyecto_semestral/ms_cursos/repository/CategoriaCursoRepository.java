package com.proyecto_semestral.ms_cursos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_cursos.entity.CategoriaCurso;

@Repository
public interface CategoriaCursoRepository extends JpaRepository<CategoriaCurso, Long> {

    Optional<CategoriaCurso> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
