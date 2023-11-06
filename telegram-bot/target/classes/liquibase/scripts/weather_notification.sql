-- liquibase formatted sql

-- changeset PY:2

create TABLE weather_notification (
  id BIGSERIAL PRIMARY KEY NOT NULL,
  chat_id BIGSERIAL NOT NULL,
  city_name varchar(256),
  reminder_date_time TIMESTAMP NOT NULL
);
