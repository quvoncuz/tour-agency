-- liquibase formatted sql

-- changeset dev:v1_002_agencies
create table agencies
(
    approved    boolean,
    rating      double precision,
    id          bigint not null
        primary key,
    owner_id    bigint
        constraint foreign_key_profile_for_agency
            references profile,
    address     varchar(255),
    city        varchar(255),
    description varchar(255),
    email       varchar(255),
    name        varchar(255),
    phone       varchar(255),
    status      varchar(255)
        constraint agency_status_check
            check ((status)::text = ANY
        ((ARRAY ['PENDING':: character varying, 'REJECTED':: character varying, 'ACCEPTED':: character varying])::text[])
)
    );