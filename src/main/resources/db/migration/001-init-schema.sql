--liquibase formatted sql

--changeset bank:001
create table users (
    id bigserial primary key,
    username varchar(100) not null unique,
    password_hash varchar(255) not null,
    role varchar(20) not null
);

create table cards (
    id bigserial primary key,
    encrypted_number varchar(512) not null unique,
    number_last4 varchar(4) not null,
    owner_id bigint not null references users(id),
    status varchar(20) not null,
    balance numeric(19,2) not null,
    expires_at date not null,
    created_at timestamp not null
);
