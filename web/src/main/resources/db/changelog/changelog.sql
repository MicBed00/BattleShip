--liquibase formatted sql

--changeset micbed:1

CREATE TABLE start_game (id   INT PRIMARY KEY, date TIMESTAMP);

--changeset micbed:2

INSERT INTO start_game (id, date) VALUES ('1', current_timestamp)
