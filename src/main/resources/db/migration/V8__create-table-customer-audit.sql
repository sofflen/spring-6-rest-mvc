drop table if exists customer_audit cascade;

create table customer_audit
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
    customer_name        varchar(50),
    email                varchar(255),
    primary key (audit_id)
);