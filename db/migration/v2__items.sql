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
    reserved    integer      not null
);