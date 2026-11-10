package com.proyecto_semestral.ms_recetas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto_semestral.ms_recetas.entity.Receta;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Long> {

    List<Receta> findByPublicadaTrue();

    List<Receta> findByCategoriaIdAndPublicadaTrue(Long categoriaId);

    List<Receta> findByTituloContainingIgnoreCaseAndPublicadaTrue(String titulo);

    List<Receta> findByTituloContainingIgnoreCaseAndCategoriaIdAndPublicadaTrue(String titulo, Long categoriaId);
}
