insert into client(name, login, password)
    values ('Ivan', '1a1', 'a1'),
            ('Karol', 'aaa', 'qwe'),
            ('Ovela', '1', '1');

insert into psychologist(id, name, estimation, status)
    values (1,'Anna',1.25,true),
    (2,'Lolla',0,false),
    (3,'Nant',2.3,true);

insert into reviews(id,text,estimation,client_id,psy_id)
    values (1,'Good',4,1,3),
            (2,'Ohh my',5,2,3),
            (3,'yoooo',3,1,1);

insert into appointment(id,data,status,client_id,psy_id)
    values(1, '10.07.2022',true,1,3),
        (2,'14.01.2022',true,3,3),
    (3, '05.10.2022', false, 1, 1);