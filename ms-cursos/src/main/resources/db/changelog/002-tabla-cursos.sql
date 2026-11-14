--liquibase formatted sql

--changeset pedro:002-tabla-cursos
CREATE TABLE cursos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    categoria_id BIGINT NOT NULL,
    imagen_url VARCHAR(2048),
    publicado BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_cursos PRIMARY KEY (id),
    CONSTRAINT fk_cursos_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias_curso (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

--rollback DROP TABLE cursos;
