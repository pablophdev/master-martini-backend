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
@Table(name = "pasos_preparacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasoPreparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receta_id", nullable = false)
    private Long recetaId;

    @Column(nullable = false)
    private Integer numero;

    @Column(length = 2000)
    private String descripcion;
}
