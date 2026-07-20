insert into authors(name)
values ('Лев Толстой'),
       ('Александр Пушкин');

insert into books(title, author_id)
values ('Война и мир', 1),
       ('Война и мир 2 том', 1),
       ('Капитанская дочка', 2);

select *
from Books b
where b.author_id = 1
  and b.title = 'Война и мир';

select *
from Books b
join authors a on b.author_id = a.id
where b.title = 'Война и мир'
and a.name = 'Лев Толстой';

--------------------------------------------

insert into players(name)
values ('Agassi'),
       ('Federer'),
       ('Nadal'),
       ('Djokovic'),
       ('Sampras');

insert into matches(player1, player2, winner)
values (1, 2, 1),
       (1, 2, 2),
       (2, 1, 1),
       (2, 3, 2),
       (3, 2, 2),
       (4, 3, 3),
       (4, 5, 4),
       (4, 3, 3),
       (3, 2, 2);