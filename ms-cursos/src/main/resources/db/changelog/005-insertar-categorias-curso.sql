--liquibase formatted sql

--changeset pedro:005-insertar-categorias-curso
INSERT INTO categorias_curso (nombre) VALUES ('Coctelería');
INSERT INTO categorias_curso (nombre) VALUES ('Repostería');
INSERT INTO categorias_curso (nombre) VALUES ('Barismo');
INSERT INTO categorias_curso (nombre) VALUES ('Técnicas básicas');

--rollback DELETE FROM categorias_curso WHERE nombre IN ('Coctelería', 'Repostería', 'Barismo', 'Técnicas básicas');
