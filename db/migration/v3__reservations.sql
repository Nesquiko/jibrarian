create table if not exists reservation
(
    id      uuid primary key,
    user_id uuid not null references users (id),
    item_id uuid not null references items (id),
    until   date not null
);
