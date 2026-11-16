--liquibase formatted sql

--changeset master-martini:006-crear-imagenes-receta
CREATE TABLE imagenes_receta (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receta_id BIGINT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_contenido VARCHAR(100) NOT NULL,
    datos LONGBLOB NOT NULL,
    creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actualizado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_imagenes_receta PRIMARY KEY (id),
    CONSTRAINT uk_imagenes_receta_receta UNIQUE (receta_id),
    CONSTRAINT fk_imagenes_receta_receta
        FOREIGN KEY (receta_id) REFERENCES recetas (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

--rollback DROP TABLE imagenes_receta;
