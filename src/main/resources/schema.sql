create table players_table
(
    id   bigserial  primary key,
    name varchar(100) not null
);


create table matches_table
(
    id      bigserial primary key,

    player1_id bigint not null,
    constraint fk_matches_player1 foreign key (player1_id) references players_table (id),

    player2_id bigint not null,
    constraint fk_matches_player2 foreign key (player2_id) references players_table (id),

    winner_id  bigint not null,
    constraint fk_matches_winner foreign key (winner_id) references players_table (id),

    constraint players_are_different check (player1_id  <> player2_id),

    constraint winner_is_player1_or_player2 check (matches_table.winner_id  = player1_id
                                                       or matches_table.winner_id = player2_id)
);

CREATE UNIQUE INDEX index_players_name ON players_table (name);