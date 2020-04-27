package ch.astina.covi.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class ExportController
{
    private final static Logger log = LoggerFactory.getLogger(ExportController.class);

    private final NamedParameterJdbcTemplate db;

    public ExportController(NamedParameterJdbcTemplate db)
    {
        this.db = db;
    }

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> export(
            @RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since,
            HttpServletResponse response)
    {
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-disposition", "attachment;filename=covid_export.csv");

        StreamingResponseBody responseBody = outputStream -> {
            try (
                CSVPrinter csvPrinter = new CSVPrinter(
                    new OutputStreamWriter(outputStream, StandardCharsets.UTF_8),
                    CSVFormat.EXCEL.withHeader(
                        "id", "participant_code",

                        // general
                        "sex", "age_range", "zip", "phone_digits", "household_size",
                        "feels_healthy", "previously_unhealthy",
                        "has_been_tested", "when_tested", "where_tested", "sought_medical_advice",
                        "test_result",
                        "works_in_health", "leaving_home_for_work",
                        "was_abroad", "was_in_contact_with_case",

                        // comorbidities
                        "is_smoker",
                        "comorbidity_highBloodPressure", "comorbidity_diabetes1", "comorbidity_diabetes2",
                        "comorbidity_obesity", "comorbidity_pregnancy", "comorbidity_immuneSystem",
                        "comorbidity_bloodCancer", "comorbidity_hiv", "comorbidity_spleen",
                        "comorbidity_kidney", "comorbidity_heartDisease", "comorbidity_liver",
                        "comorbidity_respiratory", "comorbidity_neurological",
                        "comorbidity_organTransplant",
                        // symptoms
                        "symptom_fever", "symptom_coughing", "symptom_dyspnea", "symptom_tiredness",
                        "symptom_throat", "symptom_nausea", "symptom_headache", "symptom_musclePain",
                        "symptom_runnyNose", "symptom_diarrhea", "symptom_lostTaste",

                        // previously-experienced symptoms
                        "previously_fever", "previously_coughing", "previously_dyspnea",
                        "previously_tiredness", "previously_throat", "previously_nausea",
                        "previously_headache", "previously_musclePain", "previously_runnyNose",
                        "previously_diarrhea", "previously_lostTaste",
                        "behavior",

                        "_created",
                        "_ip_addr"
                    )
                )
            ) {
                MapSqlParameterSource params = new MapSqlParameterSource();
                params.addValue("since", Timestamp.valueOf(since));
                String query = "select * from covid_submission where _created >= :since";

                db.query(query, params, rs -> {
                    try {
                        csvPrinter.printRecord(
                                rs.getString("id"),
                                rs.getString("participant_code"),

                                rs.getString("sex"),
                                rs.getString("age_range"),
                                rs.getString("zip"),
                                rs.getString("phone_digits"),
                                rs.getInt("household_size"),
                                rs.getBoolean("feels_healthy"),
                                rs.getBoolean("previously_unhealthy"),
                                rs.getBoolean("has_been_tested"),
                                rs.getString("when_tested"),
                                rs.getString("where_tested"),
                                rs.getBoolean("sought_medical_advice"),
                                rs.getString("test_result"),
                                rs.getString("works_in_health"),
                                rs.getBoolean("leaving_home_for_work"),
                                rs.getString("was_abroad"),
                                rs.getString("was_in_contact_with_case"),
                                rs.getBoolean("is_smoker"),
                                rs.getBoolean("comorbidity_highBloodPressure"),
                                rs.getBoolean("comorbidity_diabetes1"),
                                rs.getBoolean("comorbidity_diabetes2"),
                                rs.getBoolean("comorbidity_obesity"),
                                rs.getBoolean("comorbidity_pregnancy"),
                                rs.getBoolean("comorbidity_immuneSystem"),
                                rs.getBoolean("comorbidity_bloodCancer"),
                                rs.getBoolean("comorbidity_hiv"),
                                rs.getBoolean("comorbidity_spleen"),
                                rs.getBoolean("comorbidity_kidney"),
                                rs.getBoolean("comorbidity_heartDisease"),
                                rs.getBoolean("comorbidity_liver"),
                                rs.getBoolean("comorbidity_respiratory"),
                                rs.getBoolean("comorbidity_neurological"),
                                rs.getBoolean("comorbidity_organTransplant"),
                                rs.getString("symptom_fever"),
                                rs.getString("symptom_coughing"),
                                rs.getString("symptom_dyspnea"),
                                rs.getString("symptom_tiredness"),
                                rs.getString("symptom_throat"),
                                rs.getString("symptom_nausea"),
                                rs.getString("symptom_headache"),
                                rs.getString("symptom_musclePain"),
                                rs.getString("symptom_runnyNose"),
                                rs.getString("symptom_diarrhea"),
                                rs.getString("symptom_lostTaste"),
                                rs.getBoolean("previously_fever"),
                                rs.getBoolean("previously_coughing"),
                                rs.getBoolean("previously_dyspnea"),
                                rs.getBoolean("previously_tiredness"),
                                rs.getBoolean("previously_throat"),
                                rs.getBoolean("previously_nausea"),
                                rs.getBoolean("previously_headache"),
                                rs.getBoolean("previously_musclePain"),
                                rs.getBoolean("previously_runnyNose"),
                                rs.getBoolean("previously_diarrhea"),
                                rs.getBoolean("previously_lostTaste"),
                                rs.getString("behavior"),

                                rs.getString("_created"),
                                rs.getString("_ip_addr")
                        );
                    }
                    catch (IOException e) {
                        log.error("Error during CSV data export of specific row", e);
                        // not sure what to do here?
                        e.printStackTrace();
                    }
                });
            }
        };

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @CrossOrigin(origins = "*", methods = RequestMethod.GET)
    @GetMapping("/count")
    public ResponseEntity<Long> count()
    {
        AtomicReference<Long> count = new AtomicReference<>();

        db.query("select count(*) from covid_submission", rs -> {
            count.set(rs.getLong(1));
        });

        return ResponseEntity.ok(count.get());
    }
}
