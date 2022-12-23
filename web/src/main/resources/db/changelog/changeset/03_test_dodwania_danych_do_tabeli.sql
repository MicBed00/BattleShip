--liquibase formatted sql

--changeset micbed:3

INSERT INTO
    start_game (id, date, gamestatus)
VALUES
    ('1', current_timestamp, '{
      "boardsStatus" : [ ],
      "state" : "IN_PROCCESS",
      "curretnPlayer" : 1
    }'
     );