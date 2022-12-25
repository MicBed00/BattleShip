--liquibase formatted sql

--changeset micbed:3

ALTER TABLE start_game
    ALTER COLUMN id TYPE integer;