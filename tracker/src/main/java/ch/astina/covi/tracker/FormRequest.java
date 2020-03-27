package ch.astina.covi.tracker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Optional;

/**
 * See https://gist.github.com/lucnat/6578adccc0b68594df8a1c575a35b46f
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormRequest
{
    @NotNull
    public final Gender sex;

    @Min(1900)
    public final Integer yearOfBirth;

    @Size(min = 4, max = 4)
    public final String zip;

    // Last four digits of phone number
    @Size(min = 4, max = 4)
    public final String phoneDigits;

    public final Boolean feelsHealthy;

    public final Boolean hasBeenTested;

    public final LocalDate whenTested;

    @Size(max = 255)
    public final String whereTested;

    public final TestResult testResult;

    @NotNull
    public final WorksInHealth worksInHealth;

    @NotNull
    public final WasAbroad wasAbroad;

    @NotNull
    public final Boolean wasInContactWithCase;

    public final LocalDate dateContacted;

    @NotNull
    public final ChronicCondition chronicConditionType;

    public final Boolean fever;

    public final Integer feverSince;

    public final Boolean coughing;

    public final Integer coughingSince;

    public final Boolean dyspnea;

    public final Integer dyspneaSince;

    public final Boolean tiredness;

    public final Integer tirednessSince;

    public final Boolean throat;

    public final Integer throatSince;

    @JsonCreator
    public FormRequest(@JsonProperty("sex") @NotNull Gender sex,
                       @JsonProperty("yearOfBirth") @Min(1900) Integer yearOfBirth,
                       @JsonProperty("zip") @Size(min = 4, max = 4) String zip,
                       @JsonProperty("phoneDigits") @Size(min = 4, max = 4) String phoneDigits,
                       @JsonProperty("feelsHealthy") Boolean feelsHealthy,
                       @JsonProperty("hasBeenTested") Boolean hasBeenTested,
                       @JsonProperty("whenTested") LocalDate whenTested,
                       @JsonProperty("whereTested") String whereTested,
                       @JsonProperty("testResult") TestResult testResult,
                       @JsonProperty("worksInHealth") WorksInHealth worksInHealth,
                       @JsonProperty("wasAbroad") WasAbroad wasAbroad,
                       @JsonProperty("wasInContactWithCase") Boolean wasInContactWithCase,
                       @JsonProperty("dateContacted") LocalDate dateContacted,
                       @JsonProperty("chronicConditionType") ChronicCondition chronicConditionType,
                       @JsonProperty("fever") Boolean fever,
                       @JsonProperty("feverSince") Integer feverSince,
                       @JsonProperty("coughing") Boolean coughing,
                       @JsonProperty("coughingSince") Integer coughingSince,
                       @JsonProperty("dyspnea") Boolean dyspnea,
                       @JsonProperty("dyspneaSince") Integer dyspneaSince,
                       @JsonProperty("tiredness") Boolean tiredness,
                       @JsonProperty("tirednessSince") Integer tirednessSince,
                       @JsonProperty("throat") Boolean throat,
                       @JsonProperty("throatSince") Integer throatSince)
    {
        this.sex = sex;
        this.yearOfBirth = yearOfBirth;
        this.zip = zip;
        this.phoneDigits = phoneDigits;
        this.feelsHealthy = feelsHealthy;
        this.hasBeenTested = hasBeenTested;
        this.whereTested = whereTested;
        this.whenTested = whenTested;
        this.testResult = testResult;
        this.worksInHealth = Optional.ofNullable(worksInHealth).orElse(WorksInHealth.no);
        this.wasAbroad = Optional.ofNullable(wasAbroad).orElse(WasAbroad.no);
        this.wasInContactWithCase = wasInContactWithCase;
        this.dateContacted = dateContacted;
        this.chronicConditionType = Optional.ofNullable(chronicConditionType).orElse(ChronicCondition.no);
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
    }

    public enum Gender
    {
        male,
        female,
        other,
    }

    public enum TestResult
    {
        positive,
        negative,
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

    public enum ChronicCondition
    {
        no,
        heart,
        lung,
        diabetes,
        other,
    }
}
