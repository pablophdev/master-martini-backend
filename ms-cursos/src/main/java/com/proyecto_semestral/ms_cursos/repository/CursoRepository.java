package com.proyecto_semestral.ms_cursos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_cursos.entity.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByIdAndPublicadoTrue(Long id);

    List<Curso> findByPublicadoTrue();

    List<Curso> findByCategoriaIdAndPublicadoTrue(Long categoriaId);

    List<Curso> findByTituloContainingIgnoreCaseAndPublicadoTrue(String titulo);

    List<Curso> findByTituloContainingIgnoreCaseAndCategoriaIdAndPublicadoTrue(
            String titulo,
            Long categoriaId);
}
