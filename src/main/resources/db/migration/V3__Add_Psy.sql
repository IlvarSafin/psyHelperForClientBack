insert into psychologist (email, name, password, registered, status, sex, price, description)
values ('farit@email.ru', 'Fara', '$2a$12$GetL5GjFKLer.6yQtYH5lOLdiB7iRYTky3cqCDlBU5zzBn7vMOk.u', true, true, 'W', 2600, 'Описание — композиционная форма, которую используют в литературоведении и лингвистике для подробной характеристики предметов или явлений.Обобщенно, объекты описания представляют собой перцептивные паттерны - качества (формы, направления, цвет, звук, вес, температуру и т.д.), количественные аспекты (меры и множества) и их отношения, более конкретно - это предметы, процессы, жизнь, характер людей и мн. др. объекты живой, неживой природы и сами субъективные переживания.'),
       ('donat@email.ru', 'Danil', '$2a$12$GetL5GjFKLer.6yQtYH5lOLdiB7iRYTky3cqCDlBU5zzBn7vMOk.u', true, true, 'M', 1000, 'Для этой композиционной формы, в числе прочего, характерно изображение пространственных отношений, в котором также отображается пространственное соотношение элементов предмета. Это придает описанию визуальную наглядность, реализуемую применением языковых средств для описания пространства — наречий места, пространственных обозначений, данных направлений, расстояний, форм, а также соответствующих прилагательных.');

insert into psychologist (email, name, password, registered, status, sex, price, description)
values ('admin@admin.ru', 'Admin', '$2a$12$GetL5GjFKLer.6yQtYH5lOLdiB7iRYTky3cqCDlBU5zzBn7vMOk.u', true, true, 'M', 0, 'Ya admin ept');

insert into psy_role (psy_id, roles)
values (1, 1),
       (2, 1),
       (3, 2);