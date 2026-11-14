--liquibase formatted sql

--changeset pedro:004-tabla-lecciones
CREATE TABLE lecciones (
    id BIGINT NOT NULL AUTO_INCREMENT,
    curso_id BIGINT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    orden INT NOT NULL,
    video_url VARCHAR(2048) NOT NULL,
    duracion_segundos INT NOT NULL,
    CONSTRAINT pk_lecciones PRIMARY KEY (id),
    CONSTRAINT uk_lecciones_curso_orden UNIQUE (curso_id, orden),
    CONSTRAINT ck_lecciones_orden_positivo CHECK (orden > 0),
    CONSTRAINT ck_lecciones_duracion_positiva CHECK (duracion_segundos > 0),
    CONSTRAINT fk_lecciones_curso
        FOREIGN KEY (curso_id) REFERENCES cursos (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

--rollback DROP TABLE lecciones;
