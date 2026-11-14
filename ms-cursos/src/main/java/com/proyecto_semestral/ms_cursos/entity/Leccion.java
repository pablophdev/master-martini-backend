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
@Table(name = "lecciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    @Column(nullable = false)
    private Integer orden;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "duracion_segundos")
    private Integer duracionSegundos;
}
