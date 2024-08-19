create table users
(
    id                  uuid          not null primary key,
    username            varchar(16)   not null unique,
    email               varchar(64)   not null unique,
    first_name          varchar(32)   not null,
    last_name           varchar(64),
    profile_picture_url varchar(1024),
    password            varchar(4096) not null,
    email_verified_at   timestamp,
    created_at          timestamp     not null,
    updated_at          timestamp     not null,
    deleted_at          timestamp
);

create table teams
(
    id                  uuid        not null primary key,
    name                varchar(32) not null,
    description         varchar(1024),
    profile_picture_url varchar(1024),
    created_at          timestamp   not null,
    updated_at          timestamp   not null,
    deleted_at          timestamp
);

create type memberrole as enum ('OWNER', 'EDITOR', 'VIEWER');

create table team_members
(
    id         uuid       not null primary key,
    user_id    uuid       not null references users (id),
    team_id    uuid       not null references teams (id),
    role       memberrole not null,
    created_at timestamp  not null,
    updated_at timestamp  not null,
    constraint team_members_user_id_team_id_uk unique (user_id, team_id)
);

create type teaminvitationrole as enum ('EDITOR', 'VIEWER');

create table user_team_invitations
(
    id           uuid               not null primary key,
    from_user_id uuid               not null references users (id),
    to_user_id   uuid               not null references users (id),
    team_id      uuid               not null references teams (id),
    role         teaminvitationrole not null,
    is_accepted  boolean,
    responded_at timestamp,
    cancelled_at timestamp,
    created_at   timestamp          not null,
    updated_at   timestamp          not null,
    constraint user_team_invitations_from_user_id_to_user_id_team_id_uk unique (from_user_id, to_user_id, team_id)
);

create table boards
(
    id                 uuid        not null primary key,
    name               varchar(64) not null,
    description        varchar(1024),
    banner_picture_url varchar(1024),
    color_hex          varchar(7),
    owner_id           uuid        not null,
    owner_type         varchar(32) not null,
    created_at         timestamp   not null,
    updated_at         timestamp   not null,
    deleted_at         timestamp,
    check (owner_type in ('USER', 'TEAM')),
    check (color_hex ~ '^#[0-9a-f]{6}$')
);

create table columns
(
    id          uuid        not null primary key,
    position    bigint      not null,
    board_id    uuid        not null references boards (id),
    name        varchar(64) not null,
    description varchar(128),
    color_hex   varchar(7),
    created_at  timestamp   not null,
    updated_at  timestamp   not null,
    check (color_hex ~ '^#[0-9a-f]{6}$'),
    constraint columns_position_board_id_uk unique (position, board_id)
);

create table cards
(
    id         uuid          not null primary key,
    column_id  uuid          not null references columns (id),
    body       varchar(1024) not null,
    color_hex  varchar(7),
    created_at timestamp     not null,
    updated_at timestamp     not null,
    check (color_hex ~ '^#[0-9a-f]{6}$')
);

create table comments
(
    id           uuid         not null primary key,
    user_id      uuid         not null references users (id),
    body         varchar(128) not null,
    context_id   uuid         not null,
    context_type varchar(32)  not null,
    created_at   timestamp    not null,
    updated_at   timestamp    not null,
    check (context_type in ('BOARD', 'COLUMN', 'CARD'))
);

create type historyaction as enum ('CREATE', 'UPDATE', 'DELETE');

create table histories
(
    id                   uuid          not null primary key,
    user_id              uuid          not null references users (id),
    action               historyaction not null,
    context_id           uuid          not null,
    context_type         varchar(32)   not null,
    context_key          varchar(256)  not null,
    context_value_before text,
    context_value_after  text,
    created_at           timestamp     not null,
    check (context_type in ('BOARD', 'COLUMN', 'CARD'))
);