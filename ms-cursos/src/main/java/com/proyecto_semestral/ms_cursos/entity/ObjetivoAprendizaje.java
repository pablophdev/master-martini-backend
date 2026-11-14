package com.proyecto_semestral.ms_cursos.entity;

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
@Table(name = "objetivos_aprendizaje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjetivoAprendizaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Integer orden;
}
