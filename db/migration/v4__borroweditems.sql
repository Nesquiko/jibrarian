create table if not exists borrowed_items
(
    id      uuid not null primary key,
    user_id uuid not null references users (id),
    item_id uuid not null references items (id),
    until   date not null
);