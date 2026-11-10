package com.proyecto_semestral.ms_recetas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_recetas.entity.PasoPreparacion;

@Repository
public interface PasoPreparacionRepository extends JpaRepository<PasoPreparacion, Long> {

    List<PasoPreparacion> findByRecetaIdOrderByNumeroAsc(Long recetaId);

    void deleteByRecetaId(Long recetaId);
}
