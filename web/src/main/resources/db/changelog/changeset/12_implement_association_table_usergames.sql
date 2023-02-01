--liqiubase formatted sql

--changeset micbed:12


ALTER TABLE games
    DROP COLUMN user_id;

CREATE TABLE user_games (
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, game_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (game_id) REFERENCES games (id)
);
