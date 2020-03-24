package ch.astina.covi.tracker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * See https://gist.github.com/lucnat/6578adccc0b68594df8a1c575a35b46f
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormRequest
{
    @NotNull
    public final Gender sex;

    @Min(0)
    public final Integer age;

    @Size(min = 4, max = 4)
    public final String zip;

    // Last four digits of phone number
    @Size(max = 4)
    public final String phoneDigits;

    public final Boolean feelsHealthy;

    public final Boolean hasBeenTested;

    public final LocalDate whenTested;

    @Size(max = 30)
    public final String whereTested;

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
                       @JsonProperty("age") @Min(0) Integer age,
                       @JsonProperty("zip") @Size(min = 4, max = 4) String zip,
                       @JsonProperty("phoneDigits") @Size(max = 4) String phoneDigits,
                       @JsonProperty("feelsHealthy") Boolean feelsHealthy,
                       @JsonProperty("hasBeenTested") Boolean hasBeenTested,
                       @JsonProperty("whenTested") LocalDate whenTested,
                       @JsonProperty("whereTested") String whereTested,
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
        this.age = age;
        this.zip = zip;
        this.phoneDigits = phoneDigits;
        this.feelsHealthy = feelsHealthy;
        this.hasBeenTested = hasBeenTested;
        this.whereTested = whereTested;
        this.whenTested = whenTested;
        this.worksInHealth = worksInHealth;
        this.wasAbroad = wasAbroad;
        this.wasInContactWithCase = wasInContactWithCase;
        this.dateContacted = dateContacted;
        this.chronicConditionType = chronicConditionType;
        this.fever = fever;
        this.feverSince = feverSince;
        this.coughing = coughing;
        this.coughingSince = coughingSince;
        this.dyspnea = dyspnea;
        this.dyspneaSince = dyspneaSince;
        this.tiredness = tiredness;
        this.tirednessSince = tirednessSince;
        this.throat = throat;
        this.throatSince = throatSince;
    }

    public enum Gender
    {
        male,
        female,
        other,
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
