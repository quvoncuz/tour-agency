-- liquibase formatted sql

-- changeset dev:change_click_amount_type
alter table click_transactions
alter column amount type float8;