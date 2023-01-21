--liquibase formatted sql

--changeset micbed:5

CREATE TABLE application_user
(
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(200) NOT NULL
);

INSERT INTO application_user (first_name, last_name, email, password)
VALUES
    ('admin', 'admin', 'admin@admin.com', '{bcrypt}$2a$10$Ruu5GtmSVkfLeuGfz/wHUuzflCcMbwJHSBo/.Wui0EM0KIM52Gs2S');

CREATE TABLE user_role
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

INSERT INTO user_role (name)
VALUES
    ('ADMIN'),
    ('USER');

CREATE TABLE user_roles
(
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES application_user(id),
    FOREIGN KEY (role_id) REFERENCES user_role(id)
);

