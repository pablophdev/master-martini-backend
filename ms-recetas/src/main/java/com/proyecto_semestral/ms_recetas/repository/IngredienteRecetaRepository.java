package com.proyecto_semestral.ms_recetas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_recetas.entity.IngredienteReceta;

@Repository
public interface IngredienteRecetaRepository extends JpaRepository<IngredienteReceta, Long> {

    List<IngredienteReceta> findByRecetaIdOrderByOrdenAsc(Long recetaId);

    void deleteByRecetaId(Long recetaId);
}
