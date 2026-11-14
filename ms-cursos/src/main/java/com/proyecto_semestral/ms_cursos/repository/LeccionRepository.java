package com.proyecto_semestral.ms_cursos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_cursos.entity.Leccion;

@Repository
public interface LeccionRepository extends JpaRepository<Leccion, Long> {

    Optional<Leccion> findByIdAndCursoId(Long id, Long cursoId);

    boolean existsByCursoIdAndOrden(Long cursoId, Integer orden);

    boolean existsByCursoIdAndOrdenAndIdNot(Long cursoId, Integer orden, Long id);

    List<Leccion> findByCursoIdOrderByOrdenAsc(Long cursoId);

    void deleteByCursoId(Long cursoId);
}
