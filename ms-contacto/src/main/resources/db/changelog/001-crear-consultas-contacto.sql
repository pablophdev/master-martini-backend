--liquibase formatted sql

--changeset pedro:001-crear-consultas-contacto
CREATE TABLE consultas_contacto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    usuario_id BIGINT,
    email VARCHAR(254) NOT NULL,
    telefono VARCHAR(30),
    nombre_negocio VARCHAR(150) NOT NULL,
    tipo_negocio VARCHAR(100),
    tamano_negocio VARCHAR(50),
    descripcion TEXT NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
    creado_en DATETIME NOT NULL,
    CONSTRAINT pk_consultas_contacto PRIMARY KEY (id)
);

--rollback DROP TABLE consultas_contacto;
