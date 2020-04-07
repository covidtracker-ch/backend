package ch.astina.covi.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class FormController
{

    private final URI FORM_REDIRECT_URL_ERROR;
    private final URI FORM_REDIRECT_URL_SUCCESS;

    private final static Logger log = LoggerFactory.getLogger(FormController.class);

    private final NamedParameterJdbcTemplate db;

    private final Utils utils;

    public FormController(NamedParameterJdbcTemplate db, Utils utils)
    {
        this.db = db;
        this.utils = utils;

        String host = System.getenv("REDIRECT_HOST");
        
        if (host == null) {
          host = "https://www.covidtracker.ch";
        }

        this.FORM_REDIRECT_URL_ERROR = URI.create(host + "/?error=true");
        this.FORM_REDIRECT_URL_SUCCESS = URI.create(host + "/response.html");
    }

    private static URI appendUriParam(URI oldUri, String appendQuery) throws URISyntaxException {
        String newQuery = oldUri.getQuery();

        if (newQuery == null) {
            newQuery = appendQuery;
        }
        else {
            newQuery += "&" + appendQuery;
        }

        return new URI(oldUri.getScheme(), oldUri.getAuthority(),
                oldUri.getPath(), newQuery, oldUri.getFragment());
    }

    @CrossOrigin(origins = {
            "https://www.covidtracker.ch",
            "https://staging.covidtracker.ch",
            "http://localhost:4567"}, // Sam dev
            methods = RequestMethod.POST)
    @PostMapping("/form")
    public ResponseEntity<Void> form(@RequestParam(value = "participantCode", required = false) String participantCode,
                                     @RequestParam("sex") FormRequest.Gender sex,
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
                                     HttpServletRequest request)
    {
        FormRequest data = new FormRequest(
                participantCode,
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
            String code = saveSubmission(data, request);
            URI responseWithCode = appendUriParam(FORM_REDIRECT_URL_SUCCESS, "code=" + code);

            return redirect(responseWithCode);

        } catch (Exception e) {
            log.error("Error saving form submission", e);

            return redirect(FORM_REDIRECT_URL_ERROR);
        }
    }

    @CrossOrigin(origins = "*", methods = RequestMethod.POST)
    @PostMapping(value = "/save")
    public ResponseEntity<Void> save(@RequestBody @Valid FormRequest data, HttpServletRequest request)
    {
        saveSubmission(data, request);

        return ResponseEntity.accepted().build();
    }

    private String saveSubmission(@RequestBody @Valid FormRequest data, HttpServletRequest request)
    {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.registerSqlType("participant_code", Types.VARCHAR);
        params.registerSqlType("sex", Types.VARCHAR);
        params.registerSqlType("test_result", Types.VARCHAR);
        params.registerSqlType("works_in_health", Types.VARCHAR);
        params.registerSqlType("was_abroad", Types.VARCHAR);
        params.registerSqlType("chronic_condition", Types.VARCHAR);

        params.addValue("participant_code", data.participantCode);
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

        String ipAnon = utils.anonymizeIp(request.getRemoteAddr());
        params.addValue("ip_addr", ipAnon);

        String ipHash = utils.hashIp(request.getRemoteAddr());
        params.addValue("ip_hash", ipHash);

        String uaHash = utils.hashUserAgent(request);
        params.addValue("ua_hash", uaHash);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        db.update("insert into covid_submission (" +
                        "participant_code, sex, year_of_birth, zip, phone_digits, feels_healthy, has_been_tested, where_tested, when_tested, test_result, " +
                        "works_in_health, was_abroad, was_in_contact_with_case, chronic_condition, " +
                        "symptom_fever, symptom_coughing, symptom_dyspnea, symptom_tiredness, symptom_throat, " +
                        "_ip_addr, _ip_hash, _ua_hash) values (" +
                        ":participant_code, :sex, :year_of_birth, :zip, :phone_digits, :feels_healthy, :has_been_tested, :where_tested, :when_tested, :test_result, " +
                        ":works_in_health, :was_abroad, :was_in_contact_with_case, :chronic_condition, " +
                        ":symptom_fever, :symptom_coughing, :symptom_dyspnea, :symptom_tiredness, :symptom_throat, " +
                        ":ip_addr, :ip_hash, :ua_hash)",
                params, keyHolder, new String[]{"id"});

        // retrieve the participant's code, which was either the one they supplied or generated by the db
        MapSqlParameterSource codeParams = new MapSqlParameterSource();

        codeParams.addValue("id", keyHolder.getKey());
        AtomicReference<String> returnedCode = new AtomicReference<>();

        db.query("select participant_code from covid_submission where id=:id", codeParams, rs -> {
            returnedCode.set(rs.getString("participant_code"));
        });

        return returnedCode.get();
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
