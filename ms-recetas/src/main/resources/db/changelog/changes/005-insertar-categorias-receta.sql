--liquibase formatted sql

--changeset master-martini:005-insertar-categorias-receta
INSERT INTO categorias_receta (nombre) VALUES ('Cócteles');
INSERT INTO categorias_receta (nombre) VALUES ('Postres');
INSERT INTO categorias_receta (nombre) VALUES ('Repostería');
INSERT INTO categorias_receta (nombre) VALUES ('Técnicas básicas');

--rollback DELETE FROM categorias_receta WHERE nombre IN ('Cócteles', 'Postres', 'Repostería', 'Técnicas básicas');
