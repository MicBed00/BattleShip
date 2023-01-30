--liquibase formatted sql

--changeset micbed:11

CREATE UNIQUE INDEX  email_index ON users(email);

CREATE UNIQUE INDEX games_id_index ON games(id);

CREATE INDEX games_userId_index ON games(user_id);

CREATE UNIQUE INDEX game_statuses_id_index ON game_statuses(id);

CREATE INDEX game_statuses_gameId_index ON game_statuses(game_id);
