drop table if exists beer_order cascade;

drop table if exists beer_order_line cascade;

create table beer_order
(
    id           uuid not null,
    customer_ref varchar(255),
    customer_id  uuid,
    version      integer,
    created_at   timestamp(6),
    updated_at   timestamp(6),
    primary key (id),
    constraint beer_order_customer_fk foreign key (customer_id) references customer (id)
);

create table beer_order_line
(
    id                 uuid not null,
    beer_id            uuid,
    beer_order_id      uuid,
    order_quantity     integer,
    quantity_allocated integer,
    version            integer,
    created_at         timestamp(6),
    updated_at         timestamp(6),
    primary key (id),
    constraint beer_order_line_beer_fk foreign key (beer_id) references beer (id),
    constraint beer_order_line_beer_order_fk foreign key (beer_order_id) references beer_order (id)
);
