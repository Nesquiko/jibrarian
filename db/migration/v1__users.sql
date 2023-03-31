create type user_role as enum ('member', 'librarian', 'admin');

create table if not exists users
(
    id        uuid         not null primary key,
    email     varchar(255) not null unique,
    pass_hash varchar(60)  not null,
    role      user_role    not null default 'member'
);