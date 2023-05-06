CREATE TABLE games
(
    id         BIGSERIAL                NOT NULL PRIMARY KEY,
    date       TIMESTAMP with time zone NOT NULL,
    owner_game BIGINT                   NOT NULL,
    setups     jsonB                    NOT NULL
);

CREATE TABLE game_statuses
(
    id          BIGSERIAL NOT NULL PRIMARY KEY,
    status_game jsonb     NOT NULL,
    game_id     BIGSERIAL REFERENCES games (id)
);

CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(200) NOT NULL
);

CREATE TABLE users_games
(
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, game_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (game_id) REFERENCES games (id)
);



CREATE TABLE user_role
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

INSERT INTO user_role (name)
VALUES ('ADMIN'),
       ('USER');

CREATE TABLE user_roles
(
    id      SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (role_id) REFERENCES user_role (id)
);

CREATE TABLE persistent_logins
(
    username  VARCHAR(64) NOT NULL,
    series    VARCHAR(64) PRIMARY KEY,
    token     VARCHAR(64) NOT NULL,
    last_used TIMESTAMP   NOT NULL
)

