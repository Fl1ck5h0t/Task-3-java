CREATE TABLE players (
     playerId int8 PRIMARY KEY,
     nickname varchar(40) unique
);

CREATE TABLE items (
    id int8 PRIMARY KEY unique not null,
    resourceId int8,
    count int4,
    level int4,
    check ( count >= 0 and level >= 0 )
);


CREATE TABLE progresses(
    id int8 primary key unique not null,
    playerId int8 references players(playerId),
    resourceId int8,
    score int4,
    maxScore int4,
    check ( maxScore >= score )
);

CREATE TABLE currencies(
    id int8 primary key unique not null,
    resourceId int8,
    name varchar(100),
    count int4,
    check ( count >= 0 )
);

CREATE TABLE player_currency_map(
    playerId int8 references players(playerId),
    currencyId int8 references currencies(id)
);

CREATE TABLE player_item_map(
    playerId int8 references players(playerId),
    itemId int8 references items(id)
)







