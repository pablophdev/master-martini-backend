--liquibase formatted sql

--changeset master-martini:001-crear-categorias-receta
CREATE TABLE categorias_receta (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT pk_categorias_receta PRIMARY KEY (id),
    CONSTRAINT uk_categorias_receta_nombre UNIQUE (nombre)
);

--rollback DROP TABLE categorias_receta;
