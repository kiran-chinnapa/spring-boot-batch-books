create table person
(
id integer not null,
name varchar(255) not null,
location varchar(255) not null,
birth_date timestamp,
primary key(id)
);


insert into person(id,name,location,birth_date)
values(1,'kiro','bangalore',sysdate());
insert into person(id,name,location,birth_date)
values(2,'bab','henderson',sysdate());
insert into person(id,name,location,birth_date)
values(3,'alan','pitsberg',sysdate());
insert into person(id,name,location,birth_date)
values(4,'dhruvin','delaware',sysdate());

