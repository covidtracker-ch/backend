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

-- v0.0.15

alter table covid_submission
    add _ip_hash varchar(255);

alter table covid_submission
    add _ua_hash varchar(255);


-- adds participant codes and the functionality to generate them on submission

create table covid_participant_codes
(
    participant_code varchar(255) constraint covid_participant_codes_pk primary key,
    _created timestamp not null default current_timestamp
);

alter table covid_submission
    add participant_code varchar(255) references covid_participant_codes(participant_code);

CREATE OR REPLACE FUNCTION generate_participant_code() RETURNS trigger AS
$BODY$
DECLARE
    new_code VARCHAR(255);
BEGIN
    IF NEW.participant_code is null or TRIM(NEW.participant_code) = '' THEN
        LOOP
            -- iterate until we find an unused code
            -- (the space is huge,  so collisions should be very rare)

            -- the previous code only generates 0-9, A-E
            -- new_code := UPPER(SUBSTRING(MD5(''||NOW()::TEXT||RANDOM()::TEXT) FOR 6));

            SELECT array_to_string(array((
                SELECT SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ123456789' FROM mod((random()*32)::int, 32)+1 FOR 1)
                FROM generate_series(1,6))),'')
            into new_code;

            BEGIN
                INSERT INTO covid_participant_codes (participant_code) VALUES (new_code);
                NEW.participant_code = new_code;
                EXIT;
            EXCEPTION WHEN unique_violation THEN
            -- just try again
            END;
        END LOOP;
    ELSE
        -- insert the given code (ignoring the unique violation if it's already there)
        INSERT INTO covid_participant_codes (participant_code) VALUES (NEW.participant_code) ON CONFLICT DO NOTHING;
    END IF;

    RETURN NEW;
END;
$BODY$ LANGUAGE PLPGSQL;

CREATE TRIGGER add_code_to_submission
    BEFORE INSERT ON covid_submission
    FOR EACH ROW
EXECUTE PROCEDURE generate_participant_code();
