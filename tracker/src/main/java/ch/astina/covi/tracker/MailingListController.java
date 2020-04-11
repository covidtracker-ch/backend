package ch.astina.covi.tracker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.net.URI;

import static ch.astina.covi.tracker.Utils.prependLanguageCode;
import static ch.astina.covi.tracker.Utils.redirect;

@RestController
public class MailingListController {
    private final static Logger log = LoggerFactory.getLogger(FormController.class);

    private final NamedParameterJdbcTemplate db;
    private final AppProperties properties;

    public MailingListController(NamedParameterJdbcTemplate db, Utils utils, AppProperties properties)
    {
        this.db = db;
        this.properties = properties;
    }

    @CrossOrigin(origins = {
            "https://www.covidtracker.ch",
            "https://staging.covidtracker.ch",
            "https://unified.covidtracker.ch",
            "http://localhost:4567"}, // Sam dev
            methods = RequestMethod.POST)
    @PostMapping("/mailinglist/subscribe")
    public ResponseEntity<String> mailinglist(
            @Email @RequestParam(value = "email", required = true) @Size(max = 100) String email,
            @RequestParam(value = "lang", defaultValue = "de") FormRequest.UserLanguage userLang,
            HttpServletRequest request
    )
    {
        MailingListRequest data = new MailingListRequest(email);

        try {
            saveRequest(data, request);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            log.error("Error saving form submission", e);
            return ResponseEntity.status(500).body(e.toString());
        }
    }

    private void saveRequest(@RequestBody @Valid MailingListRequest data, HttpServletRequest request)
    {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", data.email);
        db.update("insert into covid_mailinglist (email) values (:email) on conflict do nothing", params);
    }
}
