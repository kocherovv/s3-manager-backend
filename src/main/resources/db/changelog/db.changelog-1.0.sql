--liquibase formatted sql

--changeset kocherovv:1
CREATE TABLE users
(
    id       bigserial primary key,
    email    varchar(255) not null unique,
    name     varchar(255) not null unique,
    password bytea,
    role     varchar      not null
);

--changeset kocherovv:2
CREATE TABLE event
(
    id         bigserial primary key,
    created_at timestamp,
    event_type varchar(255),
    file_info  jsonb  not null,
    user_id    bigint not null
        references users
);

--changeset kocherovv:3
CREATE TABLE file
(
    id         bigserial
        primary key,
    created_at timestamp,
    updated_at timestamp,
    content    bytea,
    extension  varchar(255) not null,
    name       varchar(255) not null,
    user_id    bigint
        references users
);

--changeset kocherovv:4
ALTER TABLE file
    RENAME COLUMN updated_at to modified_at;

ALTER TABLE file
    ADD COLUMN modified_by varchar(255);
ALTER TABLE file
    ADD COLUMN created_by varchar(255);

ALTER TABLE users
    ADD COLUMN modified_by varchar(255);
ALTER TABLE users
    ADD COLUMN modified_at timestamp(6);
ALTER TABLE users
    ADD COLUMN created_by varchar(255);
ALTER TABLE users
    ADD COLUMN created_at timestamp(6);
ALTER TABLE users
    ALTER COLUMN password TYPE varchar(255)
        USING password::varchar(255);

ALTER TABLE file
    DROP COLUMN content;

--changeset kocherovv:5
CREATE TABLE revision
(
    id        bigserial primary key,
    timestamp bigint
);

CREATE TABLE file_aud
(
    id          bigint not null,
    rev         bigint not null
        references revision,
    revtype     smallint,
    created_at  timestamp(6),
    created_by  varchar(255),
    modified_at timestamp(6),
    modified_by varchar(255),
    extension   varchar(255),
    name        varchar(255),
    user_id     bigint,
    primary key (rev, id)
);

create table users_aud
(
    id          bigint not null,
    rev         bigint not null
        references revision,
    revtype     smallint,
    email       varchar(255),
    created_at  timestamp(6),
    created_by  varchar(255),
    modified_at timestamp(6),
    modified_by varchar(255),
    name        varchar(255),
    password    varchar(255),
    role        varchar(255),
    primary key (rev, id)
);