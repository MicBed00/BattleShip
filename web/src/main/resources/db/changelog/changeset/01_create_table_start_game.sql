--liquibase formatted sql

--changeset micbed:1

CREATE TABLE start_game (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    date TIMESTAMP with time zone,
    game VARCHAR(150) NOT NULL
    );

