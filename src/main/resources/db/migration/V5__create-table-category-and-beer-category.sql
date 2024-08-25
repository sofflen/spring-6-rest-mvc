drop table if exists category cascade;
drop table if exists beer_category cascade;

create table category
(
    id          uuid not null,
    version     integer,
    created_at  timestamp(6),
    updated_at  timestamp(6),
    description varchar(50),
    primary key (id)
);

create table beer_category
(
    beer_id     uuid references beer (id),
    category_id uuid references category (id),
    primary key (beer_id, category_id)
)