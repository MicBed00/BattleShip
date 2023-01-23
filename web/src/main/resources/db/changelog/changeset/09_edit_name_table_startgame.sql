--liquibase formatted sql

--changeset micbed:9

ALTER TABLE start_game
RENAME TO games;