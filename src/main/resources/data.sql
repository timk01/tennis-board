insert into players_table(name)
values ('agassi'),
       ('federer'),
       ('nadal'),
       ('djokovic'),
       ('sampras');

insert into matches_table(player1_id, player2_id, winner_id)
values (1, 2, 1),
       (1, 2, 2),
       (2, 1, 1),
       (2, 3, 2),
       (3, 2, 2),
       (4, 3, 3),
       (4, 5, 4),
       (4, 3, 3),
       (3, 2, 2);


drop table if exists matches cascade;
drop table if exists players cascade;

drop table if exists matches_table cascade;
drop table if exists players_table cascade;