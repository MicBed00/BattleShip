--liquibase formatted sql

--changeset micbed:2

CREATE TABLE status_game (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    game_status jsonb NOT NULL ,
    game_id BIGSERIAL REFERENCES start_game (id)
    );

