
CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);
CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);
insert into company(id, name) values (1, 'Газпром');
insert into company(id, name) values (2, 'Лукойл');
insert into company(id, name) values (3, 'amd');
insert into company(id, name) values (4, 'nvidia');
insert into company(id, name) values (5, 'microsoft');

insert into person(id, name, company_id) VALUES (1, 'Петр', 1);
insert into person(id, name, company_id) VALUES (2, 'Олег', 1);
insert into person(id, name, company_id) VALUES (3, 'Ольга', 2);
insert into person(id, name, company_id) VALUES (4, 'Мария', 3);
insert into person(id, name, company_id) VALUES (5, 'Иван', 3);
insert into person(id, name, company_id) VALUES (6, 'Николай', 2);
insert into person(id, name, company_id) VALUES (7, 'Никита', 4);
insert into person(id, name, company_id) VALUES (8, 'Елена', 5);
insert into person(id, name, company_id) VALUES (9, 'Борис', 5);
insert into person(id, name, company_id) VALUES (10, 'Максим',3);
insert into person(id, name, company_id) VALUES (11, 'Андрей', 2);

select p.name as "Сотрудник", c.name as "Компания" from company as c
  join person as p on p.company_id=c.id WHERE c.id != 5;

select c.name, count(*)  from company c
join person p on c.id = p.company_id
group by c.name
having count(*) = (select count(*)
from person p
group by company_id
order by count(*) desc
limit 1); 