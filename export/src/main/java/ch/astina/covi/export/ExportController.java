package ch.astina.covi.export;

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

        StreamingResponseBody stream = out -> {

            String header = "id,sex,year_of_birth,zip,phone_digits,feels_healthy,has_been_tested,where_tested,when_tested,test_result," +
                    "works_in_health,was_abroad,was_in_contact_with_case,chronic_condition,symptom_fever,symptom_coughing," +
                    "symptom_dyspnea,symptom_tiredness,symptom_throat,_created,_ip_addr\r\n";
            out.write(header.getBytes());

            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("since", Timestamp.valueOf(since));

            String query = "select id,sex,year_of_birth,zip,phone_digits,feels_healthy,has_been_tested,where_tested,when_tested,test_result," +
                    "works_in_health,was_abroad,was_in_contact_with_case,chronic_condition,symptom_fever,symptom_coughing," +
                    "symptom_dyspnea,symptom_tiredness,symptom_throat,_created,_ip_addr " +
                    "from covid_submission where _created >= :since";
            db.query(query, params, rs -> {
                try {

                    String row = String.format("%s,%s,%s,%s,%s,%s,%s,\"%s\",%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,\"%s\"\r\n",
                            rs.getString("id"),
                            rs.getString("sex"),
                            rs.getString("year_of_birth"),
                            rs.getString("zip"),
                            rs.getString("phone_digits"),
                            rs.getBoolean("feels_healthy"),
                            rs.getBoolean("has_been_tested"),
                            rs.getString("where_tested"),
                            rs.getString("when_tested"),
                            rs.getString("test_result"),
                            rs.getString("works_in_health"),
                            rs.getString("was_abroad"),
                            rs.getString("was_in_contact_with_case"),
                            rs.getString("chronic_condition"),
                            rs.getString("symptom_fever"),
                            rs.getString("symptom_coughing"),
                            rs.getString("symptom_dyspnea"),
                            rs.getString("symptom_tiredness"),
                            rs.getString("symptom_throat"),
                            rs.getString("_created"),
                            rs.getString("_ip_addr")
                    );

                    out.write(row.getBytes());

                } catch (Exception e) {
                    log.error("Error during CSV data export", e);
                }
            });
        };

        return new ResponseEntity<>(stream, HttpStatus.OK);
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
