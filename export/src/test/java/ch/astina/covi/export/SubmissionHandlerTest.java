package ch.astina.covi.export;

import ch.astina.covi.common.model.FormRequest;
import ch.astina.covi.common.model.Submission;
import ch.astina.covi.common.model.SubmissionMetadata;
import ch.astina.covi.test.PostgresTestContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@PostgresTestContext
class SubmissionHandlerTest
{
    @Autowired
    private SubmissionHandler submissionHandler;

    @Autowired
    private JdbcTemplate db;

    @Test
    void contextLoads()
    {
    }

    @Test
    void handle()
    {
        Submission submission = new Submission(
                new FormRequest(
                        FormRequest.Gender.female,
                        1960,
                        "8000",
                        null,
                        true,
                        true,
                        LocalDate.of(2020, 3, 23),
                        null,
                        null,
                        FormRequest.WorksInHealth.no,
                        FormRequest.WasAbroad.no,
                        false,
                        null,
                        FormRequest.ChronicCondition.no,
                        false,
                        null,
                        true,
                        3,
                        false,
                        null,
                        false,
                        null,
                        false,
                        null
                ),
                new SubmissionMetadata(
                        ZonedDateTime.now(),
                        "127.0.0.0",
                        "08bd7b3f7d005739ab6b53fe71548ab5d65ccfca5651e1163e228dd264f3c10a",
                        "88cd2108b5347d973cf39cdf9053d7dd42704876d8c9a9bd8e2d168259d3ddf7"
                )
        );

        submissionHandler.handle(submission);

        db.query("select * from covid_submission order by _created desc limit 1", rs -> {

            assertEquals("female", rs.getString("sex"));
            assertEquals(1960, rs.getInt("year_of_birth"));
            assertEquals(LocalDate.of(2020, 3, 23), rs.getObject("when_tested", LocalDate.class));
            assertNull(rs.getObject("symptom_fever", Integer.class));
            assertEquals(3, rs.getInt("symptom_coughing"));
            assertNotNull(rs.getDate("_created"));
            assertEquals("127.0.0.0", rs.getString("_ip_addr"));
            assertEquals("08bd7b3f7d005739ab6b53fe71548ab5d65ccfca5651e1163e228dd264f3c10a", rs.getString("_ip_hash"));
            assertEquals("88cd2108b5347d973cf39cdf9053d7dd42704876d8c9a9bd8e2d168259d3ddf7", rs.getString("_ua_hash"));
        });
    }
}