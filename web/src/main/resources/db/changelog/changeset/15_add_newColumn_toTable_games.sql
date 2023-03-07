--liquibase formatted sql

--changeset micbed:15

ALTER TABLE games ADD COLUMN owner_game BIGINT;