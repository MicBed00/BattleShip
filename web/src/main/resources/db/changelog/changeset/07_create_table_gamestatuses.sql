--liqiubase formatted sql

--changeset micbed:7

CREATE TABLE game_statuses (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    status_game jsonb NOT NULL,
    game_id BIGSERIAL REFERENCES start_game (id)
);