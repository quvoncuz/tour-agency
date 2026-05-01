-- liquibase formatted sql

-- changeset dev:user_init_values
insert into profiles(id, full_name, username, email, password, role, gender, is_created_agency, is_active, created_at)
values(1, 'Admin', 'admin', 'admin@gmail.com', '$2a$12$tNFAcQ9jWFm5ItPv7XQ//.sYlgNy1Y2PKqtZjszesj/Gxm9/Z4TOi', 'ADMIN', 'MALE', true, true, now()),
      (2, 'Quvonchbek', 'quvonc', 'quvonc@gmail.com', '$2a$12$6EktIGAL1NcTZMHwiZVaMeVLJzlUxkPrF6xxIPdjwCdD.3EH3s2R.', 'USER', 'MALE', false, true, now()),
      (3, 'Quvonchbek', 'quvonch', 'quvonch@gmail.com', '$2a$12$QxiEjqe1YXYN8eEJxLchz./6nxX/bEPk9dV10En3bxNl7ySiDd4HK', 'AGENCY', 'MALE', true, true, now())
on conflict (id) do nothing
