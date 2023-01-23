--liquibase formatted sql

--changeset micbed:6

ALTER TABLE start_game
DROP COLUMN gamestatus;

ALTER TABLE start_game
ADD user_id BIGINT REFERENCES application_user(id);


