package ch.astina.covi.export;

import ch.astina.covi.common.model.FormRequest;
import ch.astina.covi.common.model.Submission;
import ch.astina.covi.common.model.SubmissionMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.sql.Types;

@Component
public class SubmissionHandler
{
    private final static Logger log = LoggerFactory.getLogger(SubmissionHandler.class);

    private final NamedParameterJdbcTemplate db;

    private final TransactionTemplate transaction;

    public SubmissionHandler(NamedParameterJdbcTemplate db, TransactionTemplate transaction)
    {
        this.db = db;
        this.transaction = transaction;
    }

    public void handle(Submission submission)
    {
        log.info("Saving submission {}", submission);

        transaction.execute(status -> {
            saveSubmission(submission);
            return null;
        });

        log.info("Saved submission {}", submission);
    }

    private void saveSubmission(Submission submission)
    {
        FormRequest data = submission.getData();
        SubmissionMetadata meta = submission.getMeta();

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.registerSqlType("sex", Types.VARCHAR);
        params.registerSqlType("test_result", Types.VARCHAR);
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

        params.addValue("created", Timestamp.from(meta.getCreatedAt().toInstant()));
        params.addValue("ip_addr", meta.getIpAnon());
        params.addValue("ip_hash", meta.getIpHash());
        params.addValue("ua_hash", meta.getUaHash());

        db.update("insert into covid_submission (" +
                        "sex, year_of_birth, zip, phone_digits, feels_healthy, has_been_tested, where_tested, when_tested, test_result, " +
                        "works_in_health, was_abroad, was_in_contact_with_case, chronic_condition, " +
                        "symptom_fever, symptom_coughing, symptom_dyspnea, symptom_tiredness, symptom_throat, " +
                        "_created, _ip_addr, _ip_hash, _ua_hash) values (" +
                        ":sex, :year_of_birth, :zip, :phone_digits, :feels_healthy, :has_been_tested, :where_tested, :when_tested, :test_result, " +
                        ":works_in_health, :was_abroad, :was_in_contact_with_case, :chronic_condition, " +
                        ":symptom_fever, :symptom_coughing, :symptom_dyspnea, :symptom_tiredness, :symptom_throat, " +
                        ":created, :ip_addr, :ip_hash, :ua_hash)",
                params);
    }
}
