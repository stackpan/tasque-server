create table users
(
    id                  uuid          not null primary key,
    username            varchar(16)   not null unique,
    email               varchar(64)   not null unique,
    first_name          varchar(32)   not null,
    last_name           varchar(64),
    profile_picture_url varchar(1024),
    password            varchar(4096) not null,
    email_verified_at   timestamptz,
    created_at          timestamptz   not null,
    updated_at          timestamptz   not null,
    deleted_at          timestamptz
);

create table teams
(
    id                  uuid        not null primary key,
    name                varchar(32) not null,
    description         varchar(1024),
    profile_picture_url varchar(1024),
    created_at          timestamptz not null,
    updated_at          timestamptz not null,
    deleted_at          timestamptz
);

create type memberrole as enum ('owner', 'editor', 'viewer');

create table team_members
(
    id         uuid        not null primary key,
    user_id    uuid        not null references users (id),
    team_id    uuid        not null references teams (id),
    role       memberrole  not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint team_members_user_id_team_id_uk unique (user_id, team_id)
);

create type teaminvitationrole as enum ('editor', 'viewer');

create table user_team_invitations
(
    id           uuid               not null primary key,
    from_user_id uuid               not null references users (id),
    to_user_id   uuid               not null references users (id),
    team_id      uuid               not null references teams (id),
    role         teaminvitationrole not null,
    is_accepted  boolean,
    responded_at timestamptz,
    cancelled_at timestamptz,
    created_at   timestamptz        not null,
    updated_at   timestamptz        not null,
    constraint user_team_invitations_from_user_id_to_user_id_team_id_uk unique (from_user_id, to_user_id, team_id)
);

create type boardownertype as enum ('user', 'team');

create table boards
(
    id                 uuid           not null primary key,
    name               varchar(64)    not null,
    description        varchar(1024),
    banner_picture_url varchar(1024),
    color_hex          varchar(7),
    owner_id           uuid           not null,
    owner_type         boardownertype not null,
    created_at         timestamptz    not null,
    updated_at         timestamptz    not null,
    deleted_at         timestamptz
);

create table columns
(
    id             uuid        not null primary key,
    board_id       uuid        not null references boards (id),
    name           varchar(64) not null,
    description    varchar(128),
    color_hex      varchar(7),
    next_column_id uuid        not null references columns (id),
    created_at     timestamptz not null,
    updated_at     timestamptz not null
);

create table cards
(
    id         uuid          not null primary key,
    column_id  uuid          not null references columns (id),
    body       varchar(1024) not null,
    color_hex  varchar(7),
    created_at timestamptz   not null,
    updated_at timestamptz   not null
);

create type commentcontexttype as enum ('board', 'column', 'card');

create table comments
(
    id           uuid               not null primary key,
    user_id      uuid               not null references users (id),
    body         varchar(128)       not null,
    context_id   uuid               not null,
    context_type commentcontexttype not null,
    created_at   timestamptz        not null,
    updated_at   timestamptz        not null
);

create type historyaction as enum ('create', 'update', 'delete');
create type historycontexttype as enum ('board', 'column', 'card');

create table histories
(
    id                   uuid               not null primary key,
    user_id              uuid               not null references users (id),
    action               historyaction      not null,
    context_id           uuid               not null,
    context_type         historycontexttype not null,
    context_key          varchar(256)       not null,
    context_value_before text,
    context_value_after  text,
    created_at           timestamptz        not null
);