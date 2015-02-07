create table event_feed(
    id AUTO_INCREMENT,
    event_text varchar(65000),
    match_id varchar(65000),
    event_type varchar(65000),
    primary key(id)
);

create table match_foul (
    id AUTO_INCREMENT, commited_by varchar(65000),
    foul_on varchar(65000),
    foul_time varchar(65000),
    match_id varchar(65000),
    primary key(id)
);

create table match_goal (
    id AUTO_INCREMENT,
    goal_type varchar(65000),
    scored_by varchar(65000),
    goal_time varchar(65000),
    match_id varchar(65000),
    team_id varchar(65000),
    primary key(id)
);

create table penalty_card (
    id AUTO_INCREMENT,
    awarded_to varchar(65000),
    card_type varchar(65000),
    match_id varchar(65000),
    primary key(id)
);
