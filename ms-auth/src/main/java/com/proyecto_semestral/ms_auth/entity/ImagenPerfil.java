package com.proyecto_semestral.ms_auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imagenes_perfil")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenPerfil {

    @Id
    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "tipo_contenido", nullable = false, length = 100)
    private String tipoContenido;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] datos;
}
