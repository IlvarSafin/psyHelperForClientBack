insert into client (email, name, password, status)
values ('rasul@email.ru', 'Rasul', '12345678', true),
       ('darya@email.ru', 'darya', '12345678', true);

insert into client_role (client_id, roles)
values (1, 0),
       (2, 0);