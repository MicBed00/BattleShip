--liquibase formatted sql

--changeset micbed:2

ALTER TABLE start_game
ADD COLUMN gamestatus jsonb;