drop table if exists beer_order_shipment cascade;

create table beer_order_shipment
(
    id              uuid not null,
    version         integer,
    created_at      timestamp(6),
    updated_at      timestamp(6),
    beer_order_id   uuid unique references beer_order (id),
    tracking_number varchar(50),
    primary key (id)
);

alter table beer_order
    add column beer_order_shipment_id uuid unique;

alter table beer_order
    add constraint beer_order_bos_id_fkey
        foreign key (beer_order_shipment_id) references beer_order_shipment (id);