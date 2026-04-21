-- liquibase formatted sql

-- changeset dev:user_init_values
insert into profiles(id, full_name, username, email, password, role, gender, is_created_agency, is_active, created_at)
values(1, 'Admin', 'admin', 'admin@gmail.com', 'Admin123', 'ADMIN', 'MALE', true, true, now()),
      (2, 'Quvonchbek', 'quvonc', 'quvonc@gmail.com', '123', 'USER', 'MALE', false, true, now()),
      (3, 'Quvonchbek', 'quvonch', 'quvonch@gmail.com', '123', 'AGENCY', 'MALE', true, true, now())
on conflict (id) do nothing
