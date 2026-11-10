package com.proyecto_semestral.ms_recetas.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "recetas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "categoria_id")
    private Long categoriaId;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "duracion_texto", length = 100)
    private String duracionTexto;

    @Column(name = "rendimiento_texto", length = 100)
    private String rendimientoTexto;

    @Column(name = "insumos_titulo", length = 150)
    private String insumosTitulo;

    @Column(name = "insumos_descripcion", length = 2000)
    private String insumosDescripcion;

    @Column(nullable = false)
    @Builder.Default
    private Boolean publicada = false;

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @UpdateTimestamp
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}
