package ch.astina.covi.tracker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class FormRequest
{
    public final Boolean isMale;

    @Min(0)
    public final Integer age;

    @Size(max = 4)
    public final String zip;

    public final Boolean hasBeenTested;

    public final LocalDate dateTested;

    public final Boolean travelled;

    public final Boolean chronicalDisease;

    public final Boolean fever;

    public final Boolean coughing;

    public final Boolean dyspnea;

    @Size(max = 100)
    public final String phoneDigits;

    @JsonCreator
    public FormRequest(@JsonProperty("isMale") Boolean isMale,
                       @JsonProperty("age") Integer age,
                       @JsonProperty("zip") String zip,
                       @JsonProperty("hasBeenTested") Boolean hasBeenTested,
                       @JsonProperty("dateTested") LocalDate dateTested,
                       @JsonProperty("travelled") Boolean travelled,
                       @JsonProperty("chronicalDisease") Boolean chronicalDisease,
                       @JsonProperty("fever") Boolean fever,
                       @JsonProperty("coughing") Boolean coughing,
                       @JsonProperty("dyspnea") Boolean dyspnea,
                       @JsonProperty("phoneDigits") String phoneDigits)
    {
        this.isMale = isMale;
        this.age = age;
        this.zip = zip;
        this.hasBeenTested = hasBeenTested;
        this.dateTested = dateTested;
        this.travelled = travelled;
        this.chronicalDisease = chronicalDisease;
        this.fever = fever;
        this.coughing = coughing;
        this.dyspnea = dyspnea;
        this.phoneDigits = phoneDigits;
    }
}
