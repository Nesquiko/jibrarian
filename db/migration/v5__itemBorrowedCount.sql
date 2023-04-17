alter table if exists items
    add borrowed integer default 0;

alter table if exists items
    drop constraint if exists total_available_reserved_check;

alter table if exists items
    add constraint total_available_reserved_check check (total = available + reserved + borrowed);