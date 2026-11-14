--liquibase formatted sql

--changeset pedro:003-tabla-objetivos
CREATE TABLE objetivos_aprendizaje (
    id BIGINT NOT NULL AUTO_INCREMENT,
    curso_id BIGINT NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    orden INT NOT NULL,
    CONSTRAINT pk_objetivos_aprendizaje PRIMARY KEY (id),
    CONSTRAINT uk_objetivos_curso_orden UNIQUE (curso_id, orden),
    CONSTRAINT ck_objetivos_orden_positivo CHECK (orden > 0),
    CONSTRAINT fk_objetivos_curso
        FOREIGN KEY (curso_id) REFERENCES cursos (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

--rollback DROP TABLE objetivos_aprendizaje;
