create table authors
(
    id   bigserial primary key,
    name varchar(255) not null
);

create table books
(
    id        bigserial primary key,
    title     varchar(255) not null,
    author_id bigint       not null,

    constraint fk_books_authors foreign key (author_id) references authors (id)
);

ALTER TABLE authors
    ADD CONSTRAINT uk_authors_name UNIQUE (name);

------------------------------------------------

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

------------------------------------------------

select a.id author, a.name author_name, b.id book, b.title book_name
from authors a
         join books b on a.id = b.author_id;

