--liquibase formatted sql

--changeset micbed:4

ALTER TABLE start_game
    DROP COLUMN id;

ALTER TABLE start_game
    ADD COLUMN id SERIAL PRIMARY KEY;