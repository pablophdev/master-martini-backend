--liquibase formatted sql

--changeset master-martini:003-crear-ingredientes-receta
CREATE TABLE ingredientes_receta (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receta_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    cantidad_texto VARCHAR(100),
    orden INT,
    CONSTRAINT pk_ingredientes_receta PRIMARY KEY (id),
    CONSTRAINT fk_ingredientes_receta_receta
        FOREIGN KEY (receta_id) REFERENCES recetas (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE INDEX idx_ingredientes_receta_orden
    ON ingredientes_receta (receta_id, orden);

--rollback DROP TABLE ingredientes_receta;
