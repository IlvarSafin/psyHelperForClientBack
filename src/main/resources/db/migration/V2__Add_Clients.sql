insert into client (email, name, password, status)
values ('rasul@email.ru', 'Rasul', '$2a$12$GetL5GjFKLer.6yQtYH5lOLdiB7iRYTky3cqCDlBU5zzBn7vMOk.u', true),
       ('darya@email.ru', 'darya', '$2a$12$GetL5GjFKLer.6yQtYH5lOLdiB7iRYTky3cqCDlBU5zzBn7vMOk.u', true);

insert into client_role (client_id, roles)
values (1, 0),
       (2, 0);