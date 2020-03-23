package ch.astina.covi.tracker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Max;
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

    @Size(max = 30)
    public final String whereTested;

    public final LocalDate whenTested;

    @NotNull
    public final WorksInHealth worksInHealth;

    @NotNull
    public final WasAbroad wasAbroad;

    public final LocalDate wasInContactWithCase;

    @NotNull
    public final ChronicCondition chronicCondition;

    public final Integer fever;

    public final Integer coughing;

    public final Integer dyspnea;

    public final Integer tiredness;

    public final Integer throat;

    @JsonCreator
    public FormRequest(@JsonProperty("sex") @NotNull Gender sex,
                       @JsonProperty("age") @Min(0) Integer age,
                       @JsonProperty("zip") @Size(min = 4, max = 4) String zip,
                       @JsonProperty("phoneDigits") @Size(max = 4) String phoneDigits,
                       @JsonProperty("feelsHealthy") Boolean feelsHealthy,
                       @JsonProperty("hasBeenTested") Boolean hasBeenTested,
                       @JsonProperty("whereTested") String whereTested,
                       @JsonProperty("whenTested") LocalDate whenTested,
                       @JsonProperty("worksInHealth") WorksInHealth worksInHealth,
                       @JsonProperty("wasAbroad") WasAbroad wasAbroad,
                       @JsonProperty("wasInContactWithCase") LocalDate wasInContactWithCase,
                       @JsonProperty("chronicCondition") ChronicCondition chronicCondition,
                       @JsonProperty("fever") Integer fever,
                       @JsonProperty("coughing") Integer coughing,
                       @JsonProperty("dyspnea") Integer dyspnea,
                       @JsonProperty("tiredness") Integer tiredness,
                       @JsonProperty("throat") Integer throat)
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
        this.chronicCondition = chronicCondition;
        this.fever = fever;
        this.coughing = coughing;
        this.dyspnea = dyspnea;
        this.tiredness = tiredness;
        this.throat = throat;
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
        none,
        heart,
        lung,
        diabetes,
        other,
    }
}
