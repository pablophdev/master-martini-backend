--liquibase formatted sql

--changeset master-martini:002-crear-recetas
CREATE TABLE recetas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    descripcion VARCHAR(1000),
    categoria_id BIGINT,
    video_url VARCHAR(500),
    imagen_url VARCHAR(500),
    duracion_texto VARCHAR(100),
    rendimiento_texto VARCHAR(100),
    insumos_titulo VARCHAR(150),
    insumos_descripcion VARCHAR(2000),
    publicada BOOLEAN NOT NULL DEFAULT FALSE,
    creado_por VARCHAR(100),
    creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME,
    CONSTRAINT pk_recetas PRIMARY KEY (id),
    CONSTRAINT fk_recetas_categoria
        FOREIGN KEY (categoria_id) REFERENCES categorias_receta (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE INDEX idx_recetas_categoria_id ON recetas (categoria_id);
CREATE INDEX idx_recetas_publicada ON recetas (publicada);
CREATE INDEX idx_recetas_titulo ON recetas (titulo);

--rollback DROP TABLE recetas;
