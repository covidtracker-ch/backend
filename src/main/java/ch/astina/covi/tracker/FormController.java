package ch.astina.covi.tracker;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.Types;

@RestController
public class FormController
{
    private final NamedParameterJdbcTemplate db;

    public FormController(NamedParameterJdbcTemplate db)
    {
        this.db = db;
    }

    @PostMapping("/save")
    public ResponseEntity<Void> handle(@RequestBody @Valid FormRequest data)
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
        params.addValue("was_in_contact_with_case", data.wasInContactWithCase);
        params.addValue("chronic_condition", data.chronicCondition);
        params.addValue("symptom_fever", data.fever);
        params.addValue("symptom_coughing", data.coughing);
        params.addValue("symptom_dyspnea", data.dyspnea);
        params.addValue("symptom_tiredness", data.tiredness);
        params.addValue("symptom_throat", data.throat);

        db.update("insert into covid_submission (" +
                        "sex, age, zip, phone_digits, feels_healthy, has_been_tested, where_tested, " +
                        "works_in_health, was_abroad, was_in_contact_with_case, chronic_condition, " +
                        "symptom_fever, symptom_coughing, symptom_dyspnea, symptom_tiredness, symptom_throat) values (" +
                        ":sex, :age, :zip, :phone_digits, :feels_healthy, :has_been_tested, :where_tested, " +
                        ":works_in_health, :was_abroad, :was_in_contact_with_case, :chronic_condition, " +
                        ":symptom_fever, :symptom_coughing, :symptom_dyspnea, :symptom_tiredness, :symptom_throat)",
                params);

        return ResponseEntity.accepted().build();
    }
}
