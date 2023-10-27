-- liquibase formatted sql

-- changeset PY:1

create TABLE notification_task (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    chat_id BIGSERIAL NOT NULL,
    reminder_text TEXT,
    reminder_date_time TIMESTAMP NOT NULL
);
