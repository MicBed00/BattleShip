--liquibase formatted sql

--changeset micbed:16

ALTER TABLE games
ADD COLUMN setups jsonb;