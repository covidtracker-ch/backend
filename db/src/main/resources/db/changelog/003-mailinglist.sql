--
-- create mailing list table
--

create table covid_mailinglist
(
    email varchar(255) not null unique,
    _created timestamp not null default current_timestamp
);
