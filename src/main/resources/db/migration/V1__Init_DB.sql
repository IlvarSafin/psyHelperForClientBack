create table appointment (
    id  serial not null,
    date timestamp,
    link varchar(255),
    status boolean,
    client_id int4,
    psy_id int4,
    primary key (id)
);

create table certificate (
    id  serial not null,
    image bytea,
    psy_id int4,
    primary key (id)
);

create table client (
    id  serial not null,
    activation_code varchar(255),
    email varchar(255),
    name varchar(255),
    password varchar(255),
    photo bytea,
    status boolean,
    primary key (id)
);

create table client_role (
    client_id int4 not null,
    roles int4
);

create table psy_role (
    psy_id int4 not null,
    roles int4
);

create table psychologist (
    id serial not null,
    activation_code varchar(255),
    description varchar(2048),
    email varchar(255),
    estimation float8,
    name varchar(255),
    password varchar(255),
    photo bytea,
    registered boolean,
    status boolean,
    primary key (id)
);

create table refreshtoken (
    id  bigserial not null,
    expiry_date timestamp not null,
    token varchar(255) not null,
    client_id int4,
    primary key (id)
);

create table review (
    id  serial not null,
    estimation int4,
    text varchar(255),
    client_id int4,
    psy_id int4,
    primary key (id)
);

alter table refreshtoken add constraint UK_or156wbneyk8noo4jstv55ii3 unique (token);
alter table appointment add constraint FK3gbqcfd3mnwwcit63lybpqcf8 foreign key (client_id) references client;
alter table appointment add constraint FK5x9jaa137uex7eby94v7clskj foreign key (psy_id) references psychologist;
alter table certificate add constraint FKohne16lc3801pqcq7obj9e5lq foreign key (psy_id) references psychologist;
alter table client_role add constraint FKjmyum5xyl9f5s6oy1xaneoe35 foreign key (client_id) references client;
alter table psy_role add constraint FK8df2cdtamqh71h0614adb7ved foreign key (psy_id) references psychologist;
alter table refreshtoken add constraint FKh82w2wygmoigmoxpv2iv5qdww foreign key (client_id) references client;
alter table review add constraint FKhr2oxqr7hu3upmi4hvg9g0ghp foreign key (client_id) references client;
alter table review add constraint FKddymrfs7e3ld6tlpbj43ni7ef foreign key (psy_id) references psychologist;
