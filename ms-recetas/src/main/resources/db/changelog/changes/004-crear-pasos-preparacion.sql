--liquibase formatted sql

--changeset master-martini:004-crear-pasos-preparacion
CREATE TABLE pasos_preparacion (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receta_id BIGINT NOT NULL,
    numero INT NOT NULL,
    descripcion VARCHAR(2000),
    CONSTRAINT pk_pasos_preparacion PRIMARY KEY (id),
    CONSTRAINT fk_pasos_preparacion_receta
        FOREIGN KEY (receta_id) REFERENCES recetas (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE INDEX idx_pasos_preparacion_numero
    ON pasos_preparacion (receta_id, numero);

--rollback DROP TABLE pasos_preparacion;
