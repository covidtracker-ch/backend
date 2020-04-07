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
