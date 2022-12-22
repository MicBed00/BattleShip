--liquibase formatted sql

--changeset micbed:1

CREATE TABLE start_game (id INT PRIMARY KEY, date TIMESTAMP with time zone);

