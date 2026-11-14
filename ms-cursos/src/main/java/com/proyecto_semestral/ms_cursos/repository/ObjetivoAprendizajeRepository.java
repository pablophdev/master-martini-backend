package com.proyecto_semestral.ms_cursos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_cursos.entity.ObjetivoAprendizaje;

@Repository
public interface ObjetivoAprendizajeRepository extends JpaRepository<ObjetivoAprendizaje, Long> {

    List<ObjetivoAprendizaje> findByCursoIdOrderByOrdenAsc(Long cursoId);

    void deleteByCursoId(Long cursoId);
}
