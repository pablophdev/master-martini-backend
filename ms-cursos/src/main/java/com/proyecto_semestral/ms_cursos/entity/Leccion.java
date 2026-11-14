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

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Integer orden;

    @Column(name = "video_url", nullable = false, length = 2048)
    private String videoUrl;

    @Column(name = "duracion_segundos", nullable = false)
    private Integer duracionSegundos;
}
