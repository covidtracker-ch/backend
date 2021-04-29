-- adds fields for vaccination questions
alter table covid_submission
    add has_been_vaccinated             varchar(10),
    add when_vaccinated_first           date,
    add when_vaccinated_second          date,
    add vaccine_type                    varchar(30);
