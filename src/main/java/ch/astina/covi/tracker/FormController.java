package ch.astina.covi.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.net.URI;
import java.sql.Types;
import java.time.LocalDate;

@RestController
public class FormController
{
    private static final URI FORM_REDIRECT_URL_ERROR = URI.create("https://covidtracker.ch/?error=true");

    private static final URI FORM_REDIRECT_URL_SUCCESS = URI.create("https://covidtracker.ch/response.html");

    private final static Logger log = LoggerFactory.getLogger(FormController.class);

    private final NamedParameterJdbcTemplate db;

    public FormController(NamedParameterJdbcTemplate db)
    {
        this.db = db;
    }

    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    @PostMapping("/form")
    public ResponseEntity<Void> form(@RequestParam("sex") FormRequest.Gender sex,
                                     @RequestParam("age") @Min(0) Integer age,
                                     @RequestParam("zip") @Size(min = 4, max = 4) String zip,
                                     @RequestParam(value = "phoneDigits", required = false) @Size(min = 4, max = 4) String phoneDigits,
                                     @RequestParam(value = "feelsHealthy", required = false) Boolean feelsHealthy,
                                     @RequestParam("hasBeenTested") Boolean hasBeenTested,
                                     @RequestParam(value = "whenTested", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate whenTested,
                                     @RequestParam(value = "whereTested", required = false) String whereTested,
                                     @RequestParam(value = "worksInHealth", required = false) FormRequest.WorksInHealth worksInHealth,
                                     @RequestParam(value = "wasAbroad", required = false) FormRequest.WasAbroad wasAbroad,
                                     @RequestParam(value = "wasInContactWithCase", required = false) Boolean wasInContactWithCase,
                                     @RequestParam("dateContacted") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateContacted,
                                     @RequestParam("chronicConditionType") FormRequest.ChronicCondition chronicConditionType,
                                     @RequestParam(value = "fever", required = false) Boolean fever,
                                     @RequestParam(value = "feverSince", required = false) Integer feverSince,
                                     @RequestParam(value = "coughing", required = false) Boolean coughing,
                                     @RequestParam(value = "coughingSince", required = false) Integer coughingSince,
                                     @RequestParam(value = "dyspnea", required = false) Boolean dyspnea,
                                     @RequestParam(value = "dyspneaSince", required = false) Integer dyspneaSince,
                                     @RequestParam(value = "tiredness", required = false) Boolean tiredness,
                                     @RequestParam(value = "tirednessSince", required = false) Integer tirednessSince,
                                     @RequestParam(value = "throat", required = false) Boolean throat,
                                     @RequestParam(value = "throatSince", required = false) Integer throatSince)
    {
        FormRequest data = new FormRequest(
                sex,
                age,
                zip,
                phoneDigits,
                feelsHealthy,
                hasBeenTested,
                whenTested,
                whereTested,
                worksInHealth,
                wasAbroad,
                wasInContactWithCase,
                dateContacted,
                chronicConditionType,
                fever,
                feverSince,
                coughing,
                coughingSince,
                dyspnea,
                dyspneaSince,
                tiredness,
                tirednessSince,
                throat,
                throatSince
        );
        try {

            saveSubmission(data);

            return redirect(FORM_REDIRECT_URL_SUCCESS);

        } catch (Exception e) {

            log.error("Error saving form submission", e);

            return redirect(FORM_REDIRECT_URL_ERROR);
        }
    }

    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    @PostMapping(value = "/save")
    public ResponseEntity<Void> save(@RequestBody @Valid FormRequest data)
    {
        saveSubmission(data);

        return ResponseEntity.accepted().build();
    }

    private void saveSubmission(@RequestBody @Valid FormRequest data)
    {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.registerSqlType("sex", Types.VARCHAR);
        params.registerSqlType("works_in_health", Types.VARCHAR);
        params.registerSqlType("was_abroad", Types.VARCHAR);
        params.registerSqlType("chronic_condition", Types.VARCHAR);

        params.addValue("sex", data.sex);
        params.addValue("age", data.age);
        params.addValue("zip", data.zip);
        params.addValue("phone_digits", data.phoneDigits);
        params.addValue("feels_healthy", data.feelsHealthy);
        params.addValue("has_been_tested", data.hasBeenTested);
        params.addValue("where_tested", data.whereTested);
        params.addValue("works_in_health", data.worksInHealth);
        params.addValue("was_abroad", data.wasAbroad);
        params.addValue("was_in_contact_with_case", data.wasInContactWithCase ? data.dateContacted : null);
        params.addValue("chronic_condition", data.chronicConditionType);
        params.addValue("symptom_fever", data.fever ? data.feverSince : null);
        params.addValue("symptom_coughing", data.coughing ? data.coughingSince : null);
        params.addValue("symptom_dyspnea", data.dyspnea ? data.dyspneaSince : null);
        params.addValue("symptom_tiredness", data.tiredness ? data.tirednessSince : null);
        params.addValue("symptom_throat", data.throat ? data.throatSince : null);

        db.update("insert into covid_submission (" +
                        "sex, age, zip, phone_digits, feels_healthy, has_been_tested, where_tested, " +
                        "works_in_health, was_abroad, was_in_contact_with_case, chronic_condition, " +
                        "symptom_fever, symptom_coughing, symptom_dyspnea, symptom_tiredness, symptom_throat) values (" +
                        ":sex, :age, :zip, :phone_digits, :feels_healthy, :has_been_tested, :where_tested, " +
                        ":works_in_health, :was_abroad, :was_in_contact_with_case, :chronic_condition, " +
                        ":symptom_fever, :symptom_coughing, :symptom_dyspnea, :symptom_tiredness, :symptom_throat)",
                params);
    }

    private ResponseEntity<Void> redirect(URI formRedirectUrlSuccess)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(formRedirectUrlSuccess);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .headers(httpHeaders)
                .build();
    }
}
