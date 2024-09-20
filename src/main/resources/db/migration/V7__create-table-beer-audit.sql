drop table if exists beer_audit cascade;

create table beer_audit
(
    audit_id             uuid not null,
    audit_created_at     timestamp(6),
    audit_updated_at     timestamp(6),
    audit_principal_name varchar(255),
    audit_event_type     varchar(255),
    id                   uuid not null,
    version              integer,
    created_at           timestamp(6),
    updated_at           timestamp(6),
    upc                  varchar(255),
    beer_name            varchar(50),
    beer_style           smallint check (beer_style between 0 and 9),
    price                numeric(38, 2),
    quantity_on_hand     integer,
    primary key (audit_id)
);