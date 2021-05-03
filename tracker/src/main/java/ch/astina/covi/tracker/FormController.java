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

import static ch.astina.covi.tracker.Utils.redirect;

@RestController
public class FormController
{
    private final static Logger log = LoggerFactory.getLogger(FormController.class);

    private final NamedParameterJdbcTemplate db;

    private final Utils utils;

    private final AppProperties properties;

    public FormController(NamedParameterJdbcTemplate db, Utils utils, AppProperties properties)
    {
        this.db = db;
        this.utils = utils;
        this.properties = properties;
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

    private static URI prependLanguageCode(URI oldUri, FormRequest.UserLanguage userLang) {
        // use the submitted language to construct a response URL w/an embedded language
        // (if it's german there's no prefix, so we return the URI unmodified)
        if (userLang != FormRequest.UserLanguage.de) {
            // reconstruct the URL with a new path, but everything else the same
            try {
                return new URI(
                        oldUri.getScheme(),
                        oldUri.getAuthority(),
                        "/" + userLang.toString() + oldUri.getPath(),
                        oldUri.getQuery(),
                        oldUri.getFragment()
                );
            } catch (URISyntaxException e) {
                // if we munge something, just return what we had before, which will still work...
                return oldUri;
            }
        }

        return oldUri;
    }

    @CrossOrigin(origins = {
            "https://www.covidtracker.ch",
            "https://staging.covidtracker.ch",
            "https://unified.covidtracker.ch",
            "http://localhost:4567"}, // Sam dev
            methods = RequestMethod.POST)
    @PostMapping("/form")
    public ResponseEntity<Void> form(
            @RequestParam(value = "participantCode", required = false) String participantCode,
            @RequestParam("sex") FormRequest.Gender sex,
            @RequestParam(value = "yearOfBirth", required = false) @Min(1900) Integer yearOfBirth,
            @RequestParam("ageRange") FormRequest.AgeRange ageRange,
            @RequestParam("zip") @Size(min = 4, max = 4) String zip,
            @RequestParam("householdSize") @Min(1) Integer householdSize,
            @RequestParam(value = "phoneDigits", required = false) @Size(min = 4, max = 4) String phoneDigits,
            @RequestParam(value = "feelsHealthy", required = false) Boolean feelsHealthy,
            @RequestParam(value = "previouslyUnhealthy", required = false) Boolean previouslyUnhealthy,
            @RequestParam(value = "hasBeenTested", required = false) Boolean hasBeenTested,
            @RequestParam(value = "whenTested", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate whenTested,
            @RequestParam(value = "whereTested", required = false) @Size(max = 255) String whereTested,
            @RequestParam(value = "hasBeenVaccinated", required = false) FormRequest.HasBeenVaccinated hasBeenVaccinated,
            @RequestParam(value = "whenVaccinatedFirst", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate whenVaccinatedFirst,
            @RequestParam(value = "whenVaccinatedSecond", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate whenVaccinatedSecond,
            @RequestParam(value = "vaccineType", required = false) FormRequest.VaccineType vaccineType,
            @RequestParam(value = "soughtMedicalAdvice", required = false) Boolean soughtMedicalAdvice,
            @RequestParam(value = "testResult", required = false) FormRequest.TestResult testResult,
            @RequestParam(value = "worksInHealth", required = false) FormRequest.WorksInHealth worksInHealth,
            @RequestParam(value = "leavingHomeForWork", required = false) Boolean leavingHomeForWork,
            @RequestParam(value = "wasAbroad", required = false) FormRequest.WasAbroad wasAbroad,
            @RequestParam(value = "wasInContactWithCase", required = false) Boolean wasInContactWithCase,
            @RequestParam(value = "dateContacted", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateContacted,
            @RequestParam(value = "isSmoker", required = false) Boolean isSmoker,

            // comorbidities
            @RequestParam(value = "comorbidity_highBloodPressure", required = false) Boolean cm_highBloodPressure,
            @RequestParam(value = "comorbidity_diabetes1", required = false) Boolean cm_diabetes1,
            @RequestParam(value = "comorbidity_diabetes2", required = false) Boolean cm_diabetes2,
            @RequestParam(value = "comorbidity_obesity", required = false) Boolean cm_obesity,
            @RequestParam(value = "comorbidity_pregnancy", required = false) Boolean cm_pregnancy,
            @RequestParam(value = "comorbidity_immuneSystem", required = false) Boolean cm_immuneSystem,
            @RequestParam(value = "comorbidity_bloodCancer", required = false) Boolean cm_bloodCancer,
            @RequestParam(value = "comorbidity_hiv", required = false) Boolean cm_hiv,
            @RequestParam(value = "comorbidity_spleen", required = false) Boolean cm_spleen,
            @RequestParam(value = "comorbidity_kidney", required = false) Boolean cm_kidney,
            @RequestParam(value = "comorbidity_heartDisease", required = false) Boolean cm_heartDisease,
            @RequestParam(value = "comorbidity_liver", required = false) Boolean cm_liver,
            @RequestParam(value = "comorbidity_respiratory", required = false) Boolean cm_respiratory,
            @RequestParam(value = "comorbidity_neurological", required = false) Boolean cm_neurological,
            @RequestParam(value = "comorbidity_organTransplant", required = false) Boolean cm_organTransplant,

            // symptoms + time of onset
            @RequestParam(value = "fever", required = false) Boolean fever,
            @RequestParam(value = "feverSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate feverSince,
            @RequestParam(value = "coughing", required = false) Boolean coughing,
            @RequestParam(value = "coughingSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate coughingSince,
            @RequestParam(value = "dyspnea", required = false) Boolean dyspnea,
            @RequestParam(value = "dyspneaSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dyspneaSince,
            @RequestParam(value = "tiredness", required = false) Boolean tiredness,
            @RequestParam(value = "tirednessSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tirednessSince,
            @RequestParam(value = "throat", required = false) Boolean throat,
            @RequestParam(value = "throatSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate throatSince,
            @RequestParam(value = "nausea", required = false) Boolean nausea,
            @RequestParam(value = "nauseaSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nauseaSince,
            @RequestParam(value = "headache", required = false) Boolean headache,
            @RequestParam(value = "headacheSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate headacheSince,
            @RequestParam(value = "musclePain", required = false) Boolean musclePain,
            @RequestParam(value = "musclePainSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate musclePainSince,
            @RequestParam(value = "runnyNose", required = false) Boolean runnyNose,
            @RequestParam(value = "runnyNoseSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate runnyNoseSince,
            @RequestParam(value = "diarrhea", required = false) Boolean diarrhea,
            @RequestParam(value = "diarrheaSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate diarrheaSince,
            @RequestParam(value = "lostTaste", required = false) Boolean lostTaste,
            @RequestParam(value = "lostTasteSince", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lostTasteSince,

            // previous symptoms (no recorded onset)
            @RequestParam(value = "previousFever", required = false) Boolean previousFever,
            @RequestParam(value = "previousCoughing", required = false) Boolean previousCoughing,
            @RequestParam(value = "previousDyspnea", required = false) Boolean previousDyspnea,
            @RequestParam(value = "previousTiredness", required = false) Boolean previousTiredness,
            @RequestParam(value = "previousThroat", required = false) Boolean previousThroat,
            @RequestParam(value = "previousNausea", required = false) Boolean previousNausea,
            @RequestParam(value = "previousHeadache", required = false) Boolean previousHeadache,
            @RequestParam(value = "previousMusclePain", required = false) Boolean previousMusclePain,
            @RequestParam(value = "previousRunnyNose", required = false) Boolean previousRunnyNose,
            @RequestParam(value = "previousDiarrhea", required = false) Boolean previousDiarrhea,
            @RequestParam(value = "previousLostTaste", required = false) Boolean previousLostTaste,

            @RequestParam(value = "lang", defaultValue = "de") FormRequest.UserLanguage userLang,

            @RequestParam("behavior") FormRequest.Behavior behavior,

            HttpServletRequest request
    )
    {
        FormRequest data = new FormRequest(
                participantCode,
                sex,
                yearOfBirth,
                ageRange,
                zip,
                householdSize,
                phoneDigits,
                feelsHealthy,
                previouslyUnhealthy,
                hasBeenTested,
                whenTested,
                whereTested,
                hasBeenVaccinated,
                whenVaccinatedFirst,
                whenVaccinatedSecond,
                vaccineType,
                soughtMedicalAdvice,
                testResult,
                worksInHealth,
                leavingHomeForWork,
                wasAbroad,
                wasInContactWithCase,
                dateContacted,
                isSmoker,

                cm_highBloodPressure,
                cm_diabetes1,
                cm_diabetes2,
                cm_obesity,
                cm_pregnancy,
                cm_immuneSystem,
                cm_bloodCancer,
                cm_hiv,
                cm_spleen,
                cm_kidney,
                cm_heartDisease,
                cm_liver,
                cm_respiratory,
                cm_neurological,
                cm_organTransplant,

                fever,
                feverSince,
                coughing,
                coughingSince,
                dyspnea,
                dyspneaSince,
                tiredness,
                tirednessSince,
                throat,
                throatSince,
                nausea,
                nauseaSince,
                headache,
                headacheSince,
                musclePain,
                musclePainSince,
                runnyNose,
                runnyNoseSince,
                diarrhea,
                diarrheaSince,
                lostTaste,
                lostTasteSince,

                previousFever,
                previousCoughing,
                previousDyspnea,
                previousTiredness,
                previousThroat,
                previousNausea,
                previousHeadache,
                previousMusclePain,
                previousRunnyNose,
                previousDiarrhea,
                previousLostTaste,

                behavior
        );
        try {
            String code = saveSubmission(data, request);

            URI finalUri = Utils.appendUriParam(properties.getRedirectUrlSuccess(), "code=" + code);
            finalUri = Utils.prependLanguageCode(finalUri, userLang);
            return redirect(finalUri);

        } catch (Exception e) {
            log.error("Error saving form submission", e);

            URI finalUri = properties.getRedirectUrlError();
            finalUri = Utils.prependLanguageCode(finalUri, userLang);
            return redirect(finalUri);
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
        params.registerSqlType("age_range", Types.VARCHAR);
        params.registerSqlType("test_result", Types.VARCHAR);
        params.registerSqlType("has_been_vaccinated", Types.VARCHAR);
        params.registerSqlType("vaccine_type", Types.VARCHAR);
        params.registerSqlType("works_in_health", Types.VARCHAR);
        params.registerSqlType("was_abroad", Types.VARCHAR);
        params.registerSqlType("behavior", Types.VARCHAR);

        params.addValue("participant_code", data.participantCode);
        params.addValue("sex", data.sex);
        params.addValue("year_of_birth", data.yearOfBirth);
        params.addValue("age_range", data.ageRange.getRealValue());
        params.addValue("zip", data.zip);
        params.addValue("household_size", data.householdSize);
        params.addValue("phone_digits", data.phoneDigits);
        params.addValue("feels_healthy", data.feelsHealthy);
        params.addValue("previously_unhealthy", data.previouslyUnhealthy);
        params.addValue("has_been_tested", data.hasBeenTested);
        params.addValue("where_tested", data.whereTested);
        params.addValue("when_tested", data.whenTested);
        params.addValue("has_been_vaccinated", data.hasBeenVaccinated);
        params.addValue("when_vaccinated_first", data.whenVaccinatedFirst);
        params.addValue("when_vaccinated_second", data.whenVaccinatedSecond);
        params.addValue("vaccine_type", data.vaccineType);
        params.addValue("sought_medical_advice", data.soughtMedicalAdvice);
        params.addValue("test_result", data.testResult);
        params.addValue("works_in_health", data.worksInHealth);
        params.addValue("leaving_home_for_work", data.leavingHomeForWork);
        params.addValue("was_abroad", data.wasAbroad);
        params.addValue("was_in_contact_with_case", Boolean.TRUE.equals(data.wasInContactWithCase) ? data.dateContacted : null);

        params.addValue("is_smoker", data.isSmoker);

        params.addValue("comorbidity_highBloodPressure", data.cm_highBloodPressure);
        params.addValue("comorbidity_diabetes1", data.cm_diabetes1);
        params.addValue("comorbidity_diabetes2", data.cm_diabetes2);
        params.addValue("comorbidity_obesity", data.cm_obesity);
        params.addValue("comorbidity_pregnancy", data.cm_pregnancy);
        params.addValue("comorbidity_immuneSystem", data.cm_immuneSystem);
        params.addValue("comorbidity_bloodCancer", data.cm_bloodCancer);
        params.addValue("comorbidity_hiv", data.cm_hiv);
        params.addValue("comorbidity_spleen", data.cm_spleen);
        params.addValue("comorbidity_kidney", data.cm_kidney);
        params.addValue("comorbidity_heartDisease", data.cm_heartDisease);
        params.addValue("comorbidity_liver", data.cm_liver);
        params.addValue("comorbidity_respiratory", data.cm_respiratory);
        params.addValue("comorbidity_neurological", data.cm_neurological);
        params.addValue("comorbidity_organTransplant", data.cm_organTransplant);

        params.addValue("symptom_fever", Boolean.TRUE.equals(data.fever) ? data.feverSince : null);
        params.addValue("symptom_coughing", Boolean.TRUE.equals(data.coughing) ? data.coughingSince : null);
        params.addValue("symptom_dyspnea", Boolean.TRUE.equals(data.dyspnea) ? data.dyspneaSince : null);
        params.addValue("symptom_tiredness", Boolean.TRUE.equals(data.tiredness) ? data.tirednessSince : null);
        params.addValue("symptom_throat", Boolean.TRUE.equals(data.throat) ? data.throatSince : null);
        params.addValue("symptom_nausea", Boolean.TRUE.equals(data.nausea) ? data.nauseaSince : null);
        params.addValue("symptom_headache", Boolean.TRUE.equals(data.headache) ? data.headacheSince : null);
        params.addValue("symptom_musclePain", Boolean.TRUE.equals(data.musclePain) ? data.musclePainSince : null);
        params.addValue("symptom_runnyNose", Boolean.TRUE.equals(data.runnyNose) ? data.runnyNoseSince : null);
        params.addValue("symptom_diarrhea", Boolean.TRUE.equals(data.diarrhea) ? data.diarrheaSince : null);
        params.addValue("symptom_lostTaste", Boolean.TRUE.equals(data.lostTaste) ? data.lostTasteSince : null);

        params.addValue("previously_fever", data.previousFever);
        params.addValue("previously_coughing", data.previousCoughing);
        params.addValue("previously_dyspnea", data.previousDyspnea);
        params.addValue("previously_tiredness", data.previousTiredness);
        params.addValue("previously_throat", data.previousThroat);
        params.addValue("previously_nausea", data.previousNausea);
        params.addValue("previously_headache", data.previousHeadache);
        params.addValue("previously_musclePain", data.previousMusclePain);
        params.addValue("previously_runnyNose", data.previousRunnyNose);
        params.addValue("previously_diarrhea", data.previousDiarrhea);
        params.addValue("previously_lostTaste", data.previousLostTaste);

        params.addValue("behavior", data.behavior);

        String ipAnon = utils.anonymizeIp(request.getRemoteAddr());
        params.addValue("ip_addr", ipAnon);

        String ipHash = utils.hashIp(request.getRemoteAddr());
        params.addValue("ip_hash", ipHash);

        String uaHash = utils.hashUserAgent(request);
        params.addValue("ua_hash", uaHash);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        db.update("insert into covid_submission (" +
                "participant_code, sex, year_of_birth, age_range, zip, household_size, phone_digits, feels_healthy, " + 
                "has_been_tested, where_tested, when_tested, test_result, " +
                "has_been_vaccinated, when_vaccinated_first, when_vaccinated_second, vaccine_type, " +
                "works_in_health, was_abroad, was_in_contact_with_case, " +
                "previously_unhealthy, sought_medical_advice, leaving_home_for_work, is_smoker, " +
                "symptom_fever, symptom_coughing, symptom_dyspnea, symptom_tiredness, symptom_throat, " +
                "symptom_nausea, symptom_headache, symptom_musclePain, symptom_runnyNose, symptom_diarrhea, symptom_lostTaste, " +

                "comorbidity_highBloodPressure, comorbidity_diabetes1, comorbidity_diabetes2, comorbidity_obesity, comorbidity_pregnancy, " +
                "comorbidity_immuneSystem, comorbidity_bloodCancer, comorbidity_hiv, comorbidity_spleen, comorbidity_kidney, " +
                "comorbidity_heartDisease, comorbidity_liver, comorbidity_respiratory, comorbidity_neurological, comorbidity_organTransplant, " +

                "previously_fever, previously_coughing, previously_dyspnea, previously_tiredness, previously_throat, " +
                "previously_nausea, previously_headache, previously_musclePain, previously_runnyNose, previously_diarrhea, previously_lostTaste, " +

                "behavior, " +

                "_ip_addr, _ip_hash, _ua_hash" +
            ") values (" +
                ":participant_code, :sex, :year_of_birth, :age_range, :zip, :household_size, :phone_digits, :feels_healthy, "+
                ":has_been_tested, :where_tested, :when_tested, :test_result, " +
                ":has_been_vaccinated, :when_vaccinated_first, :when_vaccinated_second, :vaccine_type, " +
                ":works_in_health, :was_abroad, :was_in_contact_with_case, " +

                ":previously_unhealthy, " +
                ":sought_medical_advice, " +
                ":leaving_home_for_work, " +
                ":is_smoker, " +

                ":symptom_fever, :symptom_coughing, :symptom_dyspnea, :symptom_tiredness, :symptom_throat, " +
                ":symptom_nausea, :symptom_headache, :symptom_musclePain, :symptom_runnyNose, :symptom_diarrhea, :symptom_lostTaste, " +

                ":comorbidity_highBloodPressure, " +
                ":comorbidity_diabetes1, " +
                ":comorbidity_diabetes2, " +
                ":comorbidity_obesity, " +
                ":comorbidity_pregnancy, " +
                ":comorbidity_immuneSystem, " +
                ":comorbidity_bloodCancer, " +
                ":comorbidity_hiv, " +
                ":comorbidity_spleen, " +
                ":comorbidity_kidney, " +
                ":comorbidity_heartDisease, " +
                ":comorbidity_liver, " +
                ":comorbidity_respiratory, " +
                ":comorbidity_neurological, " +
                ":comorbidity_organTransplant, " +

                ":previously_fever, " +
                ":previously_coughing, " +
                ":previously_dyspnea, " +
                ":previously_tiredness, " +
                ":previously_throat, " +
                ":previously_nausea, " +
                ":previously_headache, " +
                ":previously_musclePain, " +
                ":previously_runnyNose, " +
                ":previously_diarrhea, " +
                ":previously_lostTaste, " +

                ":behavior, " +

                ":ip_addr, :ip_hash, :ua_hash" +
            ")", params, keyHolder, new String[]{"id"});

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
    public ResponseEntity<Void> onError(HttpServletRequest req, Exception ex)
    {
        try {
            log.error("Request: " + req.getRequestURL() + " raised " + ex);
            URI finalUri = properties.getRedirectUrlError();
            finalUri = Utils.prependLanguageCode(finalUri, FormRequest.UserLanguage.valueOf(req.getParameter("lang")));
            return redirect(finalUri);
        }
        catch (Exception e) {
            log.error("Request: " + req.getRequestURL() + " raised unhandled " + e, ex);
            return redirect(properties.getRedirectUrlError());
        }
    }
}
