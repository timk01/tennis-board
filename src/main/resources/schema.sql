create table players
(
    id   serial primary key,
    name varchar(255)       not null
);


create table matches
(
    id serial primary key,

    player1 int not null,
    constraint fk_matches_player1 foreign key (player1) references players(id),

    player2 int not null,
    constraint fk_matches_player2 foreign key (player2) references players(id),

    winner int not null,
    constraint fk_matches_winner foreign key (winner) references players(id),

    constraint players_are_different check (player1 <> player2),

    constraint winner_is_player1_or_player2 check (matches.winner = player1 or matches.winner = player2)
);

CREATE UNIQUE INDEX index_players_name ON players (name);