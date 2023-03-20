create table if not exists reservations
(
    id         uuid primary key,
    user_id    uuid not null references users (id),
    item_id    uuid not null references items (id),
    until      date not null,
    deleted_at timestamp with time zone
);
