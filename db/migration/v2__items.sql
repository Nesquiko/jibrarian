create type item_type as enum ('book', 'article', 'magazine');

create table if not exists items
(
    id          uuid         not null primary key,
    title       varchar(255) not null,
    author      varchar(255) not null,
    description text         not null,
    language    varchar(10)  not null,
    genre       varchar(255) not null,
    isbn        varchar(255),
    item_type   item_type    not null,
    pages       integer      not null,
    total       integer      not null,
    available   integer      not null,
    reserved    integer      not null,
    image       bytea,

    constraint available_check check (available >= 0),
    constraint reserved_check check (reserved >= 0),
    constraint total_check check (total >= 0),
    constraint total_available_reserved_check check (total = available + reserved)
);