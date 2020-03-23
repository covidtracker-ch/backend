create table covid_submission
(
    id                       bigserial
        constraint covid_submission_pk
            primary key,
    sex                      varchar(10) not null,
    age                      int         not null,
    zip                      varchar(4)  not null,
    phone_digits             varchar(4),
    feels_healthy            bool        not null,
    has_been_tested          bool        not null,
    where_tested             varchar(30),
    when_tested              date,
    works_in_health          varchar(10),
    was_abroad               varchar(10),
    was_in_contact_with_case date,
    chronic_condition        varchar(10),
    symptom_fever            int,
    symptom_coughing         int,
    symptom_dyspnea          int,
    symptom_tiredness        int,
    symptom_throat           int,
    _created                 timestamp   not null default current_timestamp
)