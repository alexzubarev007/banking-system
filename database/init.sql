-- Applied only when PostgreSQL initializes an empty data directory.
CREATE TYPE gender AS ENUM ('MALE', 'FEMALE');
CREATE TYPE role AS ENUM ('ADMIN', 'CLIENT');
CREATE TYPE operationtype AS ENUM ('PUT', 'WITHDRAW');
CREATE TABLE users (
    id uuid PRIMARY KEY,
    name varchar(255) NOT NULL,
    gender gender NOT NULL,
    age integer NOT NULL CHECK (age >= 0),
    haircolor varchar(255) NOT NULL
);
CREATE TABLE friends (
    user1_id uuid NOT NULL REFERENCES users(id),
    user2_id uuid NOT NULL REFERENCES users(id),
    PRIMARY KEY (user1_id, user2_id),
    CHECK (user1_id <> user2_id)
);
CREATE TABLE authentifications (
    id uuid PRIMARY KEY,
    login varchar(255) NOT NULL UNIQUE,
    password_hash varchar(255) NOT NULL,
    role role NOT NULL,
    user_id uuid UNIQUE REFERENCES users(id),
    CHECK ((role = 'ADMIN' AND user_id IS NULL) OR (role = 'CLIENT' AND user_id IS NOT NULL))
);
CREATE TABLE accounts (
    id uuid PRIMARY KEY,
    balance numeric(19,2) NOT NULL CHECK (balance >= 0),
    user_id uuid NOT NULL REFERENCES users(id)
);
CREATE INDEX accounts_user_idx ON accounts(user_id);
CREATE TABLE operations (
    id uuid PRIMARY KEY,
    type operationtype NOT NULL,
    money numeric(19,2) NOT NULL CHECK (money > 0),
    time timestamp(6) NOT NULL,
    account_id uuid NOT NULL REFERENCES accounts(id)
);
CREATE INDEX operations_account_time_idx ON operations(account_id, time);
