package ch.astina.covi.tracker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

/**
 * See https://gist.github.com/lucnat/6578adccc0b68594df8a1c575a35b46f
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormRequest
{
    public final String participantCode;

    @NotNull
    public final Gender sex;

    @Min(1900)
    public final Integer yearOfBirth;

    @NotNull
    public final AgeRange ageRange;

    @Size(min = 4, max = 4)
    public final String zip;

    @Min(1)
    public final Integer householdSize;

    // Last four digits of phone number
    @Size(min = 4, max = 4)
    public final String phoneDigits;

    public final Boolean feelsHealthy;

    public final Boolean previouslyUnhealthy;

    public final Boolean hasBeenTested;

    public final LocalDate whenTested;

    public final HasBeenVaccinated hasBeenVaccinated;
    public final LocalDate whenVaccinatedFirst;
    public final LocalDate whenVaccinatedSecond;
    public final VaccineType vaccineType;

    @Size(max = 255)
    public final String whereTested;

    public final Boolean soughtMedicalAdvice;

    public final TestResult testResult;

    @NotNull
    public final WorksInHealth worksInHealth;

    public final Boolean leavingHomeForWork;

    @NotNull
    public final WasAbroad wasAbroad;

    @NotNull
    public final Boolean wasInContactWithCase;

    public final LocalDate dateContacted;

    public final Boolean isSmoker;

    public final Boolean cm_highBloodPressure;
    public final Boolean cm_diabetes1;
    public final Boolean cm_diabetes2;
    public final Boolean cm_obesity;
    public final Boolean cm_pregnancy;
    public final Boolean cm_immuneSystem;
    public final Boolean cm_bloodCancer;
    public final Boolean cm_hiv;
    public final Boolean cm_spleen;
    public final Boolean cm_kidney;
    public final Boolean cm_heartDisease;
    public final Boolean cm_liver;
    public final Boolean cm_respiratory;
    public final Boolean cm_neurological;
    public final Boolean cm_organTransplant;

    public final Boolean fever;
    public final LocalDate feverSince;

    public final Boolean coughing;
    public final LocalDate coughingSince;

    public final Boolean dyspnea;
    public final LocalDate dyspneaSince;

    public final Boolean tiredness;
    public final LocalDate tirednessSince;

    public final Boolean throat;
    public final LocalDate throatSince;

    public final Boolean nausea;
    public final LocalDate nauseaSince;

    public final Boolean headache;
    public final LocalDate headacheSince;

    public final Boolean musclePain;
    public final LocalDate musclePainSince;

    public final Boolean runnyNose;
    public final LocalDate runnyNoseSince;

    public final Boolean diarrhea;
    public final LocalDate diarrheaSince;

    public final Boolean lostTaste;
    public final LocalDate lostTasteSince;

    public final Boolean previousFever;
    public final Boolean previousCoughing;
    public final Boolean previousDyspnea;
    public final Boolean previousTiredness;
    public final Boolean previousThroat;
    public final Boolean previousNausea;
    public final Boolean previousHeadache;
    public final Boolean previousMusclePain;
    public final Boolean previousRunnyNose;
    public final Boolean previousDiarrhea;
    public final Boolean previousLostTaste;

    public final Behavior behavior;

    @JsonCreator
    public FormRequest(
            @JsonProperty("participantCode") String participantCode,
            @JsonProperty("sex") @NotNull Gender sex,
            @JsonProperty("yearOfBirth") @Min(1900) Integer yearOfBirth,
            @JsonProperty("ageRange") @NotNull AgeRange ageRange,
            @JsonProperty("zip") @Size(min = 4, max = 4) String zip,
            @JsonProperty("householdSize") @Min(1) Integer householdSize,
            @JsonProperty("phoneDigits") @Size(min = 4, max = 4) String phoneDigits,
            @JsonProperty("feelsHealthy") Boolean feelsHealthy,
            @JsonProperty("previouslyUnhealthy") Boolean previouslyUnhealthy,
            @JsonProperty("hasBeenTested") Boolean hasBeenTested,
            @JsonProperty("whenTested") LocalDate whenTested,
            @JsonProperty("whereTested") String whereTested,
            @JsonProperty("hasBeenVaccinated") HasBeenVaccinated hasBeenVaccinated,
            @JsonProperty("whenVaccinatedFirst") LocalDate whenVaccinatedFirst,
            @JsonProperty("whenVaccinatedSecond") LocalDate whenVaccinatedSecond,
            @JsonProperty("vaccineType") VaccineType vaccineType,
            @JsonProperty("soughtMedicalAdvice") Boolean soughtMedicalAdvice,
            @JsonProperty("testResult") TestResult testResult,
            @JsonProperty("worksInHealth") WorksInHealth worksInHealth,
            @JsonProperty("leavingHomeForWork") Boolean leavingHomeForWork,
            @JsonProperty("wasAbroad") WasAbroad wasAbroad,
            @JsonProperty("wasInContactWithCase") Boolean wasInContactWithCase,
            @JsonProperty("dateContacted") LocalDate dateContacted,
            @JsonProperty("isSmoker") Boolean isSmoker,

            // comorbidities
            @JsonProperty("comorbidity_highBloodPressure") Boolean cm_highBloodPressure,
            @JsonProperty("comorbidity_diabetes1") Boolean cm_diabetes1,
            @JsonProperty("comorbidity_diabetes2") Boolean cm_diabetes2,
            @JsonProperty("comorbidity_obesity") Boolean cm_obesity,
            @JsonProperty("comorbidity_pregnancy") Boolean cm_pregnancy,
            @JsonProperty("comorbidity_immuneSystem") Boolean cm_immuneSystem,
            @JsonProperty("comorbidity_bloodCancer") Boolean cm_bloodCancer,
            @JsonProperty("comorbidity_hiv") Boolean cm_hiv,
            @JsonProperty("comorbidity_spleen") Boolean cm_spleen,
            @JsonProperty("comorbidity_kidney") Boolean cm_kidney,
            @JsonProperty("comorbidity_heartDisease") Boolean cm_heartDisease,
            @JsonProperty("comorbidity_liver") Boolean cm_liver,
            @JsonProperty("comorbidity_respiratory") Boolean cm_respiratory,
            @JsonProperty("comorbidity_neurological") Boolean cm_neurological,
            @JsonProperty("comorbidity_organTransplant") Boolean cm_organTransplant,

            // symptoms
            @JsonProperty("fever") Boolean fever,
            @JsonProperty("feverSince") LocalDate feverSince,
            @JsonProperty("coughing") Boolean coughing,
            @JsonProperty("coughingSince") LocalDate coughingSince,
            @JsonProperty("dyspnea") Boolean dyspnea,
            @JsonProperty("dyspneaSince") LocalDate dyspneaSince,
            @JsonProperty("tiredness") Boolean tiredness,
            @JsonProperty("tirednessSince") LocalDate tirednessSince,
            @JsonProperty("throat") Boolean throat,
            @JsonProperty("throatSince") LocalDate throatSince,
            @JsonProperty("nausea") Boolean nausea,
            @JsonProperty("nauseaSince") LocalDate nauseaSince,
            @JsonProperty("headache") Boolean headache,
            @JsonProperty("headacheSince") LocalDate headacheSince,
            @JsonProperty("musclePain") Boolean musclePain,
            @JsonProperty("musclePainSince") LocalDate musclePainSince,
            @JsonProperty("runnyNose") Boolean runnyNose,
            @JsonProperty("runnyNoseSince") LocalDate runnyNoseSince,
            @JsonProperty("diarrhea") Boolean diarrhea,
            @JsonProperty("diarrheaSince") LocalDate diarrheaSince,
            @JsonProperty("lostTaste") Boolean lostTaste,
            @JsonProperty("lostTasteSince") LocalDate lostTasteSince,

            // previous symptoms
            @JsonProperty("previousFever") Boolean previousFever,
            @JsonProperty("previousCoughing") Boolean previousCoughing,
            @JsonProperty("previousDyspnea") Boolean previousDyspnea,
            @JsonProperty("previousTiredness") Boolean previousTiredness,
            @JsonProperty("previousThroat") Boolean previousThroat,
            @JsonProperty("previousNausea") Boolean previousNausea,
            @JsonProperty("previousHeadache") Boolean previousHeadache,
            @JsonProperty("previousMusclePain") Boolean previousMusclePain,
            @JsonProperty("previousRunnyNose") Boolean previousRunnyNose,
            @JsonProperty("previousDiarrhea") Boolean previousDiarrhea,
            @JsonProperty("previousLostTaste") Boolean previousLostTaste,

            @JsonProperty("behavior") Behavior behavior
    )
    {
        this.participantCode = participantCode;
        this.sex = sex;
        this.yearOfBirth = yearOfBirth;
        this.ageRange = ageRange;
        this.zip = zip;
        this.householdSize = householdSize;
        this.phoneDigits = phoneDigits;
        this.feelsHealthy = feelsHealthy;
        this.previouslyUnhealthy = previouslyUnhealthy;
        this.hasBeenTested = hasBeenTested;
        this.whereTested = whereTested;
        this.whenTested = whenTested;
        this.hasBeenVaccinated = hasBeenVaccinated;
        this.whenVaccinatedFirst = whenVaccinatedFirst;
        this.whenVaccinatedSecond = whenVaccinatedSecond;
        this.vaccineType = vaccineType;
        this.soughtMedicalAdvice = soughtMedicalAdvice;
        this.testResult = testResult;
        this.worksInHealth = Optional.ofNullable(worksInHealth).orElse(WorksInHealth.no);
        this.leavingHomeForWork = leavingHomeForWork;
        this.wasAbroad = Optional.ofNullable(wasAbroad).orElse(WasAbroad.no);
        this.wasInContactWithCase = wasInContactWithCase;
        this.dateContacted = dateContacted;
        this.isSmoker = isSmoker;

        this.cm_highBloodPressure = cm_highBloodPressure;
        this.cm_diabetes1 = cm_diabetes1;
        this.cm_diabetes2 = cm_diabetes2;
        this.cm_obesity = cm_obesity;
        this.cm_pregnancy = cm_pregnancy;
        this.cm_immuneSystem = cm_immuneSystem;
        this.cm_bloodCancer = cm_bloodCancer;
        this.cm_hiv = cm_hiv;
        this.cm_spleen = cm_spleen;
        this.cm_kidney = cm_kidney;
        this.cm_heartDisease = cm_heartDisease;
        this.cm_liver = cm_liver;
        this.cm_respiratory = cm_respiratory;
        this.cm_neurological = cm_neurological;
        this.cm_organTransplant = cm_organTransplant;

        this.fever = Optional.ofNullable(fever).orElse(feverSince != null);
        this.feverSince = feverSince;
        this.coughing = Optional.ofNullable(coughing).orElse(coughingSince != null);
        this.coughingSince = coughingSince;
        this.dyspnea = Optional.ofNullable(dyspnea).orElse(dyspneaSince != null);
        this.dyspneaSince = dyspneaSince;
        this.tiredness = Optional.ofNullable(tiredness).orElse(tirednessSince != null);
        this.tirednessSince = tirednessSince;
        this.throat = Optional.ofNullable(throat).orElse(throatSince != null);
        this.throatSince = throatSince;

        this.nausea = Optional.ofNullable(nausea).orElse(nauseaSince != null);
        this.nauseaSince = nauseaSince;
        this.headache = Optional.ofNullable(headache).orElse(headacheSince != null);
        this.headacheSince = headacheSince;
        this.musclePain = Optional.ofNullable(musclePain).orElse(musclePainSince != null);
        this.musclePainSince = musclePainSince;
        this.runnyNose = Optional.ofNullable(runnyNose).orElse(runnyNoseSince != null);
        this.runnyNoseSince = runnyNoseSince;
        this.diarrhea = Optional.ofNullable(diarrhea).orElse(diarrheaSince != null);
        this.diarrheaSince = diarrheaSince;
        this.lostTaste = Optional.ofNullable(lostTaste).orElse(lostTasteSince != null);
        this.lostTasteSince = lostTasteSince;

        this.previousFever = previousFever;
        this.previousCoughing = previousCoughing;
        this.previousDyspnea = previousDyspnea;
        this.previousTiredness = previousTiredness;
        this.previousThroat = previousThroat;
        this.previousNausea = previousNausea;
        this.previousHeadache = previousHeadache;
        this.previousMusclePain = previousMusclePain;
        this.previousRunnyNose = previousRunnyNose;
        this.previousDiarrhea = previousDiarrhea;
        this.previousLostTaste = previousLostTaste;

        this.behavior = behavior;
    }

    public enum Gender
    {
        male,
        female,
        other,
    }

    public enum AgeRange
    {
        age0_10("0-10"),
        age11_20("11-20"),
        age21_30("21-30"),
        age31_40("31-40"),
        age41_50("41-50"),
        age51_60("51-60"),
        age61_70("61-70"),
        age71_80("71-80"),
        age81_90("81-90"),
        age90plus("90+")
        ;

        private String realValue;

        AgeRange(String s) {
            this.realValue = s;
        }

        public static AgeRange getByValue(String value) {
            for (AgeRange r : values()) {
                if (r.realValue.equals(value)) {
                    return r;
                }
            }

            throw new IllegalArgumentException("Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
        }

        @JsonValue
        public String getRealValue() {
            return realValue;
        }
    }

    public enum TestResult
    {
        positive,
        negative,
    }

    public enum HasBeenVaccinated
    {
        yes,
        no,
        declined
    }

    public enum VaccineType
    {
        pfizer,
        moderna,
        johnson_johnson
    }

    public enum WorksInHealth
    {
        no,
        hospital,
        private_practice,
        ems,
        other,
    }

    public enum WasAbroad
    {
        no,
        italy,
        spain,
        france,
        germany,
        other,
    }

    public enum Behavior
    {
        isolation,
        self_isolation,
        precaution_isolation,
        social_distancing,
        typical_activity
    }

    public enum UserLanguage
    {
        en,
        de,
        fr,
        it
    }


    private static class AgeRangeStringToEnumConverter implements Converter<String, FormRequest.AgeRange> {
        @Override
        public FormRequest.AgeRange convert(String source) {
            return FormRequest.AgeRange.getByValue(source);
        }
    }

    @Configuration
    public static class WebConfig implements WebMvcConfigurer {
        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new AgeRangeStringToEnumConverter());
        }
    }
}
