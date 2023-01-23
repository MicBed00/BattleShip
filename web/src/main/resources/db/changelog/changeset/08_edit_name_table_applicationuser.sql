--liquibase formatted sql

--changeset micbed:8

ALTER TABLE application_user
RENAME TO users;

