--liquibase formatted sql

--changeset pedro:002-perfil-usuario
ALTER TABLE usuarios
    ADD COLUMN nombre_negocio VARCHAR(150) NULL,
    ADD COLUMN tipo_negocio VARCHAR(100) NULL,
    ADD COLUMN tamano_negocio VARCHAR(50) NULL,
    ADD COLUMN foto_perfil_url VARCHAR(2048) NULL;

--rollback ALTER TABLE usuarios DROP COLUMN foto_perfil_url, DROP COLUMN tamano_negocio, DROP COLUMN tipo_negocio, DROP COLUMN nombre_negocio;

--changeset pedro:003-imagenes-perfil
CREATE TABLE imagenes_perfil (
    usuario_id BIGINT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_contenido VARCHAR(100) NOT NULL,
    datos LONGBLOB NOT NULL,
    CONSTRAINT pk_imagenes_perfil PRIMARY KEY (usuario_id),
    CONSTRAINT fk_imagenes_perfil_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

--rollback DROP TABLE imagenes_perfil;
