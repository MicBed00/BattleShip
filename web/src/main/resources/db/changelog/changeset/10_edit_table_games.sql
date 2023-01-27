--liquibase formatted sql

--changeset micbed:10

ALTER TABLE games
DROP COLUMN user_id;

ALTER TABLE games
ADD user_id BIGINT REFERENCES users(id);
