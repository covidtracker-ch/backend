package ch.astina.covi.tracker;

import ch.astina.covi.common.model.FormRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.net.URI;
import java.time.LocalDate;

@RestController
public class FormController
{
    private static final URI FORM_REDIRECT_URL_ERROR = URI.create("https://www.covidtracker.ch/?error=true");

    private static final URI FORM_REDIRECT_URL_SUCCESS = URI.create("https://www.covidtracker.ch/response.html");

    private final static Logger log = LoggerFactory.getLogger(FormController.class);

    private final SubmissionHandler submissionHandler;

    public FormController(SubmissionHandler submissionHandler)
    {
        this.submissionHandler = submissionHandler;
    }

    @CrossOrigin(origins = {
            "https://www.covidtracker.ch",
            "https://staging.covidtracker.ch",
            "http://localhost:4567"}, // Sam dev
            methods = RequestMethod.POST)
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
                                     HttpServletRequest request)
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
    public ResponseEntity<Void> save(@RequestBody @Valid FormRequest data, HttpServletRequest request)
    {
        saveSubmission(data, request);

        return ResponseEntity.accepted().build();
    }

    private void saveSubmission(FormRequest data, HttpServletRequest request)
    {
        submissionHandler.handle(data, request);
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
