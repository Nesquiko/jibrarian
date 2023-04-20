create table if not exists borrowed_items
(
    id         uuid not null primary key,
    user_id    uuid not null references users (id) on delete cascade,
    item_id    uuid not null references items (id) on delete cascade,
    until      date not null,
    deleted_at timestamp with time zone
);