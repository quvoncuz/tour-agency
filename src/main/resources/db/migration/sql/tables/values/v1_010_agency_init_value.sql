-- liquibase formatted sql

-- changeset dev:agency_init_values
insert into agencies(approved, rating, id, owner_id, address, city, description, email, name, phone, status)
values (true, 0.0, 1, 3, 'Tashkent sh. Yunusobod t. Amir Temur st. 108', 'Toshkent', 'Tours to Europe and Japan', 'tourtoeurope@gmail.com', 'TOURtoE', '+998787787878', 'ACCEPTED')
on conflict (id) do nothing;