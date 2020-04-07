--
-- alters survey definition post-merge
--

alter table covid_submission
    add age_range                       varchar(6),
    add household_size                  int,
    add leaving_home_for_work           bool,
    add is_smoker                       bool,
    add sought_medical_advice           bool,

    add comorbidity_highBloodPressure   bool,
    add comorbidity_diabetes1           bool,
    add comorbidity_diabetes2           bool,
    add comorbidity_obesity             bool,
    add comorbidity_pregnancy           bool,
    add comorbidity_immuneSystem        bool,
    add comorbidity_bloodCancer         bool,
    add comorbidity_hiv                 bool,
    add comorbidity_spleen              bool,
    add comorbidity_kidney              bool,
    add comorbidity_heartDisease        bool,
    add comorbidity_liver               bool,
    add comorbidity_respiratory         bool,
    add comorbidity_neurological        bool,
    add comorbidity_organTransplant    bool,

    -- extra symptoms
    -- items with '_xxx' will be converted from ints to dates
    add symptom_fever_xxx               date,
    add symptom_coughing_xxx            date,
    add symptom_dyspnea_xxx             date,
    add symptom_tiredness_xxx           date,
    add symptom_throat_xxx              date,
    add symptom_nausea                  date,
    add symptom_headache                date,
    add symptom_musclePain              date,
    add symptom_runnyNose               date,
    add symptom_diarrhea                date,
    add symptom_lostTaste               date,

    -- previous symptoms
    add previously_unhealthy            bool,

    -- previous symptoms
    add previously_fever                bool,
    add previously_coughing             bool,
    add previously_dyspnea              bool,
    add previously_tiredness            bool,
    add previously_throat               bool,
    add previously_nausea               bool,
    add previously_headache             bool,
    add previously_musclePain           bool,
    add previously_runnyNose            bool,
    add previously_diarrhea             bool,
    add previously_lostTaste            bool,

    add behavior                        varchar(50);

-- migrate over year_of_birth into age_range
update covid_submission set age_range=(
    case
        when (date_part('year', now()) - year_of_birth) between 0 and 10 then '0-10'
        when (date_part('year', now()) - year_of_birth) between 11 and 20 then '11-20'
        when (date_part('year', now()) - year_of_birth) between 21 and 30 then '21-30'
        when (date_part('year', now()) - year_of_birth) between 31 and 40 then '31-40'
        when (date_part('year', now()) - year_of_birth) between 41 and 50 then '41-50'
        when (date_part('year', now()) - year_of_birth) between 51 and 60 then '51-60'
        when (date_part('year', now()) - year_of_birth) between 61 and 70 then '61-70'
        when (date_part('year', now()) - year_of_birth) between 71 and 80 then '71-80'
        when (date_part('year', now()) - year_of_birth) between 81 and 90 then '81-90'
        when (date_part('year', now()) - year_of_birth) > 90 then '90+'
        end
    );

alter table covid_submission
    alter column age_range set not null,
    alter column year_of_birth drop not null;

-- migrate existing symptom fields' "days since symptom" into dates
update covid_submission set
    symptom_fever_xxx=_created - ('1 day'::interval * symptom_fever),
    symptom_coughing_xxx=_created - ('1 day'::interval * symptom_coughing),
    symptom_dyspnea_xxx=_created - ('1 day'::interval * symptom_dyspnea),
    symptom_tiredness_xxx=_created - ('1 day'::interval * symptom_tiredness),
    symptom_throat_xxx=_created - ('1 day'::interval * symptom_throat);

-- swap out temporary fields for our new ones
alter table covid_submission
    drop column symptom_fever,
    drop column symptom_coughing,
    drop column symptom_dyspnea,
    drop column symptom_tiredness,
    drop column symptom_throat;
alter table covid_submission rename column symptom_fever_xxx to symptom_fever;
alter table covid_submission rename column symptom_coughing_xxx to symptom_coughing;
alter table covid_submission rename column symptom_dyspnea_xxx to symptom_dyspnea;
alter table covid_submission rename column symptom_tiredness_xxx to symptom_tiredness;
alter table covid_submission rename column symptom_throat_xxx to symptom_throat;
