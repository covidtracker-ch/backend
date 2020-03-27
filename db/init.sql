create table covid_submission
(
    id                       bigserial
        constraint covid_submission_pk
            primary key,
    sex                      varchar(30) not null,
    year_of_birth            int         not null,
    zip                      varchar(4)  not null,
    phone_digits             varchar(4),
    feels_healthy            bool        not null,
    has_been_tested          bool,
    where_tested             varchar(255),
    when_tested              date,
    test_result              varchar(30),
    works_in_health          varchar(30),
    was_abroad               varchar(30),
    was_in_contact_with_case date,
    chronic_condition        varchar(30),
    symptom_fever            int,
    symptom_coughing         int,
    symptom_dyspnea          int,
    symptom_tiredness        int,
    symptom_throat           int,
    _created                 timestamp   not null default current_timestamp,
    _ip_addr                 varchar(50) not null
);

create index covid_submission__created
    on covid_submission (_created);
