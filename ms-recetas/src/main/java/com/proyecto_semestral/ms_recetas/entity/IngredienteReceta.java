package com.proyecto_semestral.ms_recetas.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ingredientes_receta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredienteReceta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receta_id", nullable = false)
    private Long recetaId;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "cantidad_texto", length = 100)
    private String cantidadTexto;

    @Column(name = "orden")
    private Integer orden;
}
