package com.proyecto_semestral.ms_cursos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_cursos.entity.ImagenCurso;

@Repository
public interface ImagenCursoRepository extends JpaRepository<ImagenCurso, Long> {

    Optional<ImagenCurso> findByCursoId(Long cursoId);
}
