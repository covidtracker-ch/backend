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

import javax.servlet.ServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.net.URI;
import java.sql.Types;
import java.time.LocalDate;

@RestController
public class FormController
{
    private static final URI FORM_REDIRECT_URL_ERROR = URI.create("https://www.covidtracker.ch/?error=true");

    private static final URI FORM_REDIRECT_URL_SUCCESS = URI.create("https://www.covidtracker.ch/response.html");

    private final static Logger log = LoggerFactory.getLogger(FormController.class);

    private final NamedParameterJdbcTemplate db;

    public FormController(NamedParameterJdbcTemplate db)
    {
        this.db = db;
    }

    @CrossOrigin(origins = "https://www.covidtracker.ch", methods = RequestMethod.POST)
    @PostMapping("/form")
    public ResponseEntity<Void> form(@RequestParam("sex") FormRequest.Gender sex,
                                     @RequestParam("yearOfBirth") @Min(1900) Integer yearOfBirth,
                                     @RequestParam("zip") @Size(min = 4, max = 4) String zip,
                                     @RequestParam(value = "phoneDigits", required = false) @Size(min = 4, max = 4) String phoneDigits,
                                     @RequestParam(value = "feelsHealthy", required = false) Boolean feelsHealthy,
                                     @RequestParam(value = "hasBeenTested", required = false) Boolean hasBeenTested,
                                     @RequestParam(value = "whenTested", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate whenTested,
                                     @RequestParam(value = "whereTested", required = false) @Size(max = 255) String whereTested,
                                     @RequestParam(value = "testResult", required = false) FormRequest.TestResult testResult,
                                     @RequestParam(value = "worksInHealth", required = false) FormRequest.WorksInHealth worksInHealth,
                                     @RequestParam(value = "wasAbroad", required = false) FormRequest.WasAbroad wasAbroad,
                                     @RequestParam(value = "wasInContactWithCase", required = false) Boolean wasInContactWithCase,
                                     @RequestParam(value = "dateContacted", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateContacted,
                                     @RequestParam(value = "chronicConditionType", required = false) FormRequest.ChronicCondition chronicConditionType,
                                     @RequestParam(value = "fever", required = false) Boolean fever,
                                     @RequestParam(value = "feverSince", required = false) Integer feverSince,
                                     @RequestParam(value = "coughing", required = false) Boolean coughing,
                                     @RequestParam(value = "coughingSince", required = false) Integer coughingSince,
                                     @RequestParam(value = "dyspnea", required = false) Boolean dyspnea,
                                     @RequestParam(value = "dyspneaSince", required = false) Integer dyspneaSince,
                                     @RequestParam(value = "tiredness", required = false) Boolean tiredness,
                                     @RequestParam(value = "tirednessSince", required = false) Integer tirednessSince,
                                     @RequestParam(value = "throat", required = false) Boolean throat,
                                     @RequestParam(value = "throatSince", required = false) Integer throatSince,
                                     ServletRequest request)
    {
        FormRequest data = new FormRequest(
                sex,
                yearOfBirth,
                zip,
                phoneDigits,
                feelsHealthy,
                hasBeenTested,
                whenTested,
                whereTested,
                testResult,
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

            saveSubmission(data, request);

            return redirect(FORM_REDIRECT_URL_SUCCESS);

        } catch (Exception e) {

            log.error("Error saving form submission", e);

            return redirect(FORM_REDIRECT_URL_ERROR);
        }
    }

    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    @PostMapping(value = "/save")
    public ResponseEntity<Void> save(@RequestBody @Valid FormRequest data, ServletRequest request)
    {
        saveSubmission(data, request);

        return ResponseEntity.accepted().build();
    }

    private void saveSubmission(@RequestBody @Valid FormRequest data, ServletRequest request)
    {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.registerSqlType("sex", Types.VARCHAR);
        params.registerSqlType("works_in_health", Types.VARCHAR);
        params.registerSqlType("was_abroad", Types.VARCHAR);
        params.registerSqlType("chronic_condition", Types.VARCHAR);

        params.addValue("sex", data.sex);
        params.addValue("year_of_birth", data.yearOfBirth);
        params.addValue("zip", data.zip);
        params.addValue("phone_digits", data.phoneDigits);
        params.addValue("feels_healthy", data.feelsHealthy);
        params.addValue("has_been_tested", data.hasBeenTested);
        params.addValue("where_tested", data.whereTested);
        params.addValue("when_tested", data.whenTested);
        params.addValue("test_result", data.testResult);
        params.addValue("works_in_health", data.worksInHealth);
        params.addValue("was_abroad", data.wasAbroad);
        params.addValue("was_in_contact_with_case", Boolean.TRUE.equals(data.wasInContactWithCase) ? data.dateContacted : null);
        params.addValue("chronic_condition", data.chronicConditionType);
        params.addValue("symptom_fever", Boolean.TRUE.equals(data.fever) ? data.feverSince : null);
        params.addValue("symptom_coughing", Boolean.TRUE.equals(data.coughing) ? data.coughingSince : null);
        params.addValue("symptom_dyspnea", Boolean.TRUE.equals(data.dyspnea) ? data.dyspneaSince : null);
        params.addValue("symptom_tiredness", Boolean.TRUE.equals(data.tiredness) ? data.tirednessSince : null);
        params.addValue("symptom_throat", Boolean.TRUE.equals(data.throat) ? data.throatSince : null);

        String ipAnon = Utils.anonymizeIp(request.getRemoteAddr());
        params.addValue("ip_addr", ipAnon);

        db.update("insert into covid_submission (" +
                        "sex, year_of_birth, zip, phone_digits, feels_healthy, has_been_tested, where_tested, when_tested, test_result, " +
                        "works_in_health, was_abroad, was_in_contact_with_case, chronic_condition, " +
                        "symptom_fever, symptom_coughing, symptom_dyspnea, symptom_tiredness, symptom_throat, " +
                        "_ip_addr) values (" +
                        ":sex, :year_of_birth, :zip, :phone_digits, :feels_healthy, :has_been_tested, :where_tested, :when_tested, :test_result, " +
                        ":works_in_health, :was_abroad, :was_in_contact_with_case, :chronic_condition, " +
                        ":symptom_fever, :symptom_coughing, :symptom_dyspnea, :symptom_tiredness, :symptom_throat, " +
                        ":ip_addr)",
                params);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> onError()
    {
        return redirect(FORM_REDIRECT_URL_ERROR);
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
