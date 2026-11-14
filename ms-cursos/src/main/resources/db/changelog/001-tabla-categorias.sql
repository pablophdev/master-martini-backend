--liquibase formatted sql

--changeset pedro:001-tabla-categorias
CREATE TABLE categorias_curso (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT pk_categorias_curso PRIMARY KEY (id),
    CONSTRAINT uk_categorias_curso_nombre UNIQUE (nombre)
);

--rollback DROP TABLE categorias_curso;
