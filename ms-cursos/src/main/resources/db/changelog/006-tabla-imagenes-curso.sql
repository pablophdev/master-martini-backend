--liquibase formatted sql

--changeset pedro:006-tabla-imagenes-curso
CREATE TABLE imagenes_curso (
    id BIGINT NOT NULL AUTO_INCREMENT,
    curso_id BIGINT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_contenido VARCHAR(100) NOT NULL,
    datos LONGBLOB NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_imagenes_curso PRIMARY KEY (id),
    CONSTRAINT uk_imagenes_curso_curso UNIQUE (curso_id),
    CONSTRAINT fk_imagenes_curso_curso
        FOREIGN KEY (curso_id) REFERENCES cursos (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

--rollback DROP TABLE imagenes_curso;
